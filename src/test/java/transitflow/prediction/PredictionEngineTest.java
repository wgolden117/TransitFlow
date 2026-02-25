package transitflow.prediction;

import org.junit.jupiter.api.Test;
import transitflow.domain.delay.DelayEvent;
import transitflow.domain.delay.DelayType;
import transitflow.domain.route.*;
import transitflow.domain.shipment.Shipment;
import transitflow.delivery.StandardDeliveryPolicy;
import transitflow.simulation.SimulationState;
import transitflow.transport.truck.TruckTransport;
import transitflow.simulation.SimulationEngine;
import transitflow.delivery.DeliveryEstimateService;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that PredictionEngine operates exclusively on
 * prediction snapshots and does not mutate live simulation state.
 */
class PredictionEngineTest {

    @Test
    void predictionEngineDoesNotMutateSimulationState() {
        // Arrange: live simulation
        Shipment liveShipment = createSingleSegmentShipment();
        SimulationState liveState = new SimulationState(
                Instant.parse("2026-01-01T00:00:00Z"),
                List.of(liveShipment)
        );

        liveState.addDelayEvent(new DelayEvent(
                DelayType.WEATHER,
                Duration.ofHours(3),
                liveState.getCurrentTime(),
                "GLOBAL",
                "Snowstorm"
        ));

        PredictionSnapshot snapshot =
                PredictionSnapshotFactory.fromSimulationState(liveState);

        SimulationEngine simulationEngine = new SimulationEngine();
        DeliveryEstimateService deliveryService = new DeliveryEstimateService();

        PredictionEngine engine =
                new PredictionEngine(simulationEngine, deliveryService);

        // Act: run prediction
        engine.predict(snapshot, Duration.ofHours(6));

        // Assert: live state unchanged
        assertEquals(0, liveShipment.getCurrentSegmentIndex());
        assertTrue(liveShipment.hasMoreSegments());
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
