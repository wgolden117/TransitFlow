package transitflow.prediction;

import org.junit.jupiter.api.Test;
import transitflow.domain.delay.DelayEvent;
import transitflow.domain.delay.DelayType;
import transitflow.domain.route.*;
import transitflow.domain.shipment.Shipment;
import transitflow.delivery.StandardDeliveryPolicy;
import transitflow.simulation.SimulationEngine;
import transitflow.simulation.SimulationState;
import transitflow.transport.truck.TruckTransport;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests snapshot isolation guarantees for prediction.
 *
 * These tests ensure prediction logic can safely operate on
 * cloned simulation state without mutating live operational truth.
 */
class PredictionSnapshotTest {

    @Test
    void predictionSnapshotDoesNotMutateSimulationState() {
        // Arrange: live simulation state
        Shipment liveShipment = createSingleSegmentShipment();
        SimulationState liveState = new SimulationState(
                Instant.parse("2026-01-01T00:00:00Z"),
                List.of(liveShipment)
        );

        liveState.addDelayEvent(new DelayEvent(
                DelayType.WEATHER,
                Duration.ofHours(4),
                liveState.getCurrentTime(),
                null,
                "Winter storm",
                null,
                null
        ));

        // Create snapshot
        PredictionSnapshot snapshot =
                PredictionSnapshotFactory.fromSimulationState(liveState);

        SimulationEngine engine = new SimulationEngine();

        // Act: advance ONLY the snapshot
        engine.tick(
                new SimulationState(
                        snapshot.getSnapshotTime(),
                        snapshot.getShipments()
                ),
                Duration.ofHours(6)
        );

        // Assert: live simulation state unchanged
        assertEquals(0, liveShipment.getCurrentSegmentIndex());
        assertTrue(liveShipment.hasMoreSegments());

        // Assert: snapshot has its own shipment instance
        assertNotSame(
                liveShipment,
                snapshot.getShipments().get(0),
                "Snapshot shipment must be a deep copy"
        );

        // Assert: delay events preserved
        assertEquals(1, snapshot.getDelayEvents().size());
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
}
