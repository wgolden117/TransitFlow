package transitflow.simulation;

import org.junit.jupiter.api.Test;
import transitflow.domain.route.*;
import transitflow.domain.shipment.Shipment;
import transitflow.delivery.StandardDeliveryPolicy;
import transitflow.transport.truck.TruckTransport;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SimulationEngineTest {

    /**
     * Verifies that a shipment does NOT advance to the next route segment
     * when the elapsed simulated time is less than the base transit time
     * required to complete the current segment.
     *
     * <p>This test ensures that shipment progression is strictly time-based
     * and prevents premature advancement through route segments. In realistic
     * freight operations, a shipment cannot complete a transportation leg
     * unless sufficient transit time has elapsed.</p>
     *
     * <p>By advancing the simulation clock by fewer hours than the transport
     * mode's base transit time (truck = 6 hours), the test confirms that:
     * <ul>
     *   <li>The shipment remains on its current segment</li>
     *   <li>No segment advancement occurs</li>
     *   <li>The shipment correctly reports remaining segments</li>
     * </ul>
     * </p>
     *
     * <p>This behavior is critical for accurate delay modeling, deterministic
     * simulation behavior, and trustworthy ETA predictions.</p>
     */
    @Test
    void shipmentDoesNotAdvanceSegmentIfInsufficientTimePasses() {
        // Arrange
        Terminal origin = new Terminal(
                "CHI",
                "Chicago",
                new StandardDeliveryPolicy(ZoneId.of("America/Chicago"), LocalTime.of(6, 0))
        );

        Terminal destination = new Terminal(
                "DAL",
                "Dallas",
                new StandardDeliveryPolicy(ZoneId.of("America/Chicago"), LocalTime.of(6, 0))
        );

        Segment segment = new Segment(
                origin,
                destination,
                new TruckTransport()
        );

        Route route = new Route(origin, destination, List.of(segment));
        Shipment shipment = new Shipment("TRACK123", route);

        SimulationState state = new SimulationState(
                Instant.parse("2026-01-01T00:00:00Z"),
                List.of(shipment)
        );

        SimulationEngine engine = new SimulationEngine();

        // Act
        engine.tick(state, Duration.ofHours(2)); // less than truck transit time

        // Assert
        assertEquals(0, shipment.getCurrentSegmentIndex());
        assertTrue(shipment.hasMoreSegments());
    }
}
