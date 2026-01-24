package transitflow.prediction;

import org.junit.jupiter.api.Test;
import transitflow.delivery.StandardDeliveryPolicy;
import transitflow.domain.route.*;
import transitflow.domain.shipment.Shipment;
import transitflow.simulation.SimulationState;
import transitflow.transport.truck.TruckTransport;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies arrival semantics:
 *
 * - Terminal arrival time is predicted correctly
 * - Customer delivery time is derived via delivery policy
 */
class ArrivalEstimateTest {

    @Test
    void predictsTerminalArrivalAndCustomerDelivery() {
        // Arrange
        Shipment shipment = createSingleSegmentShipment();

        SimulationState liveState = new SimulationState(
                Instant.parse("2026-01-01T00:00:00Z"),
                List.of(shipment)
        );

        PredictionSnapshot snapshot =
                PredictionSnapshotFactory.fromSimulationState(liveState);

        PredictionEngine engine = new PredictionEngine();

        Terminal destination = shipment.getFinalDestination();

        // Act
        ArrivalEstimate estimate =
                engine.predictArrivalEstimate(snapshot, destination);

        // Assert
        assertEquals(
                Instant.parse("2026-01-01T06:00:00Z"),
                estimate.getTerminalArrival(),
                "Terminal arrival should match transit time"
        );

        assertEquals(
                Instant.parse("2026-01-01T06:00:00Z"),
                estimate.getCustomerDelivery(),
                "Delivery should be same-day when before cut-off"
        );
    }

    /* ---------------------------------------------------------
       Test helpers
       --------------------------------------------------------- */

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
