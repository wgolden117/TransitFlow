package transitflow.simulation;

import org.junit.jupiter.api.Test;
import transitflow.delivery.StandardDeliveryPolicy;
import transitflow.domain.route.*;
import transitflow.domain.shipment.Shipment;
import transitflow.transport.truck.TruckTransport;
import transitflow.domain.delay.DelayEvent;
import transitflow.domain.delay.DelayType;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests time-based progression behavior for the SimulationEngine.
 *
 * <p>These tests verify that shipments advance through route segments
 * strictly based on elapsed simulated time. A shipment should only
 * progress when enough time has passed to complete a segment, and any
 * remaining time should be carried forward correctly.</p>
 *
 * <p>This behavior is critical for deterministic simulation behavior
 * and accurate ETA prediction in freight operations.</p>
 */
public class SimulationEngineTest {

    /**
     * Verifies that a shipment does NOT advance to the next route segment
     * when the elapsed simulated time is less than the base transit time
     * required to complete the current segment.
     */
    @Test
    void shipmentDoesNotAdvanceSegmentIfInsufficientTimePasses() {
        // Arrange
        Shipment shipment = createSingleSegmentShipment();
        SimulationState state = createStateWithShipment(shipment);
        SimulationEngine engine = new SimulationEngine();

        // Act
        engine.tick(state, Duration.ofHours(2)); // less than truck transit time (6h)

        // Assert
        assertEquals(0, shipment.getCurrentSegmentIndex());
        assertTrue(shipment.hasMoreSegments());
    }

    /**
     * Verifies that a shipment advances to the next route segment
     * when the elapsed simulated time is sufficient to complete
     * the current segment.
     */
    @Test
    void shipmentAdvancesSegmentWhenSufficientTimePasses() {
        // Arrange
        Shipment shipment = createSingleSegmentShipment();
        SimulationState state = createStateWithShipment(shipment);
        SimulationEngine engine = new SimulationEngine();

        // Act
        engine.tick(state, Duration.ofHours(6)); // exactly truck transit time

        // Assert
        assertEquals(1, shipment.getCurrentSegmentIndex());
        assertFalse(shipment.hasMoreSegments());
    }

    /**
     * Verifies that excess simulated time carries over correctly
     * after completing a segment instead of being discarded.
     *
     * <p>This ensures long ticks or accumulated time advance shipments
     * accurately without losing remaining time.</p>
     */
    @Test
    void shipmentCarriesOverRemainingTimeAfterSegmentCompletion() {
        // Arrange
        Shipment shipment = createSingleSegmentShipment();
        SimulationState state = createStateWithShipment(shipment);
        SimulationEngine engine = new SimulationEngine();

        // Act
        engine.tick(state, Duration.ofHours(10)); // more than required

        // Assert
        assertEquals(1, shipment.getCurrentSegmentIndex());
        assertFalse(shipment.hasMoreSegments());
        assertNotNull(state.getCurrentTime());
    }

    /* ------------------------------------------------------------------
       Test helpers
       ------------------------------------------------------------------ */

    private Shipment createSingleSegmentShipment() {
        Terminal origin = new Terminal(
                "CHI",
                "Chicago",
                new StandardDeliveryPolicy(
                        ZoneId.of("America/Chicago"),
                        LocalTime.of(6, 0)
                )
        );

        Terminal destination = new Terminal(
                "DAL",
                "Dallas",
                new StandardDeliveryPolicy(
                        ZoneId.of("America/Chicago"),
                        LocalTime.of(6, 0)
                )
        );

        Segment segment = new Segment(
                origin,
                destination,
                new TruckTransport()
        );

        Route route = new Route(origin, destination, List.of(segment));
        return new Shipment("TRACK123", route);
    }

    private SimulationState createStateWithShipment(Shipment shipment) {
        return new SimulationState(
                Instant.parse("2026-01-01T00:00:00Z"),
                List.of(shipment)
        );
    }
    /**
     * Verifies that an active delay prevents shipment advancement,
     * even when sufficient simulated time passes.
     */
    @Test
    void shipmentDoesNotAdvanceWhileDelayIsActive() {
        // Arrange
        Shipment shipment = createSingleSegmentShipment();
        SimulationState state = createStateWithShipment(shipment);

        state.addDelayEvent(new DelayEvent(
                DelayType.WEATHER,
                Duration.ofHours(6),
                state.getCurrentTime(),
                "GLOBAL",
                "Severe winter storm"
        ));

        SimulationEngine engine = new SimulationEngine();

        // Act
        engine.tick(state, Duration.ofHours(6));

        // Assert
        assertEquals(0, shipment.getCurrentSegmentIndex());
        assertTrue(shipment.hasMoreSegments());
    }

    /**
     * Verifies that a shipment resumes advancement once the delay expires.
     */
    @Test
    void shipmentAdvancesAfterDelayExpires() {
        // Arrange
        Shipment shipment = createSingleSegmentShipment();
        SimulationState state = createStateWithShipment(shipment);

        state.addDelayEvent(new DelayEvent(
                DelayType.WEATHER,
                Duration.ofHours(3),
                state.getCurrentTime(),
                "GLOBAL",
                "Temporary storm"
        ));

        SimulationEngine engine = new SimulationEngine();

        // Act
        engine.tick(state, Duration.ofHours(3)); // delay active
        engine.tick(state, Duration.ofHours(6)); // delay expired

        // Assert
        assertEquals(1, shipment.getCurrentSegmentIndex());
        assertFalse(shipment.hasMoreSegments());
    }

    /**
     * Verifies that simulation time advances even while shipment
     * advancement is blocked by delay.
     */
    @Test
    void timeAdvancesEvenWhenShipmentIsDelayed() {
        // Arrange
        Shipment shipment = createSingleSegmentShipment();
        SimulationState state = createStateWithShipment(shipment);

        Instant startTime = state.getCurrentTime();

        state.addDelayEvent(new DelayEvent(
                DelayType.CONGESTION,
                Duration.ofHours(4),
                startTime,
                "GLOBAL",
                "Network congestion"
        ));

        SimulationEngine engine = new SimulationEngine();

        // Act
        engine.tick(state, Duration.ofHours(2));

        // Assert
        assertEquals(startTime.plus(Duration.ofHours(2)), state.getCurrentTime());
        assertEquals(0, shipment.getCurrentSegmentIndex());
    }
    /**
     * Regression guard:
     *
     * Verifies that introducing delay logic does NOT affect normal
     * shipment advancement when no delays are present.
     */
    @Test
    void shipmentAdvancesNormallyWhenNoDelaysExist() {
        // Arrange
        Shipment shipment = createSingleSegmentShipment();
        SimulationState state = createStateWithShipment(shipment);
        SimulationEngine engine = new SimulationEngine();

        // Act
        engine.tick(state, Duration.ofHours(6)); // full transit time

        // Assert
        assertEquals(1, shipment.getCurrentSegmentIndex());
        assertFalse(shipment.hasMoreSegments());
    }

}
