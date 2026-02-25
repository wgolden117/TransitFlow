package transitflow.prediction;

import org.junit.jupiter.api.Test;
import transitflow.delivery.StandardDeliveryPolicy;
import transitflow.domain.route.*;
import transitflow.domain.shipment.Shipment;
import transitflow.integration.weather.*;
import transitflow.simulation.*;
import transitflow.transport.truck.TruckTransport;
import transitflow.delivery.DeliveryEstimateService;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies that HIGH severity weather increases
 * predicted terminal arrival time.
 *
 * <p>This ensures that weather integration has
 * measurable impact on predictive simulation.</p>
 */
class WeatherImpactPredictionTest {

    @Test
    void highSeverityWeatherIncreasesArrivalTime() {

        // Arrange baseline shipment
        Shipment shipment = createSingleSegmentShipment();

        SimulationState state = new SimulationState(
                Instant.parse("2026-01-01T00:00:00Z"),
                List.of(shipment)
        );

        PredictionSnapshot snapshot =
                PredictionSnapshotFactory.fromSimulationState(state);

        // Stub WeatherClient returning HIGH severity
        WeatherClient stubClient = terminal ->
                new WeatherForecast(WeatherSeverity.HIGH);

        WeatherDelayService weatherService =
                new WeatherDelayService(stubClient);

        DelayCoordinator coordinator =
                new DelayCoordinator(weatherService);

        PredictionEngine engine =
                new PredictionEngine(
                        new SimulationEngine(),
                        new DeliveryEstimateService(),
                        coordinator
                );

        // Act
        ArrivalEstimate estimate =
                engine.predictArrivalEstimate(
                        snapshot,
                        shipment.getFinalDestination()
                );

        // Assert
        Instant baselineArrival =
                Instant.parse("2026-01-01T06:00:00Z");

        assertTrue(
                estimate.getTerminalArrival().isAfter(baselineArrival),
                "HIGH severity weather should increase arrival time"
        );
    }

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

    /**
     * Verifies that LOW severity weather does not alter
     * predicted terminal arrival time.
     *
     * <p>This confirms that non-disruptive weather conditions
     * do not inject delays and that baseline transit behavior
     * remains unchanged.</p>
     */
    @Test
    void lowSeverityWeatherDoesNotChangeArrivalTime() {

        Shipment shipment = createSingleSegmentShipment();

        SimulationState state = new SimulationState(
                Instant.parse("2026-01-01T00:00:00Z"),
                List.of(shipment)
        );

        PredictionSnapshot snapshot =
                PredictionSnapshotFactory.fromSimulationState(state);

        WeatherClient stubClient = terminal ->
                new WeatherForecast(WeatherSeverity.LOW);

        WeatherDelayService weatherService =
                new WeatherDelayService(stubClient);

        DelayCoordinator coordinator =
                new DelayCoordinator(weatherService);

        PredictionEngine engine =
                new PredictionEngine(
                        new SimulationEngine(),
                        new DeliveryEstimateService(),
                        coordinator
                );

        ArrivalEstimate estimate =
                engine.predictArrivalEstimate(
                        snapshot,
                        shipment.getFinalDestination()
                );

        Instant expected =
                Instant.parse("2026-01-01T06:00:00Z");

        assertEquals(
                expected,
                estimate.getTerminalArrival(),
                "LOW severity weather should not change arrival time"
        );
    }

    /**
     * Verifies that a weather delay eventually expires and
     * shipment progression resumes normally.
     *
     * <p>This test simulates a HIGH severity forecast on the first
     * evaluation (injecting a delay), followed by LOW severity
     * thereafter. The shipment should be temporarily delayed,
     * but ultimately complete once the injected delay expires.</p>
     *
     * <p>This protects against permanent blocking and ensures
     * delay deduplication and expiration logic function correctly.</p>
     */
    @Test
    void weatherDelayEventuallyExpiresAndShipmentCompletes() {

        Shipment shipment = createSingleSegmentShipment();

        SimulationState state = new SimulationState(
                Instant.parse("2026-01-01T00:00:00Z"),
                List.of(shipment)
        );

        PredictionSnapshot snapshot =
                PredictionSnapshotFactory.fromSimulationState(state);

        // Stub client: HIGH once, then LOW afterward
        WeatherClient stubClient = new WeatherClient() {

            private boolean firstCall = true;

            @Override
            public WeatherForecast getForecast(Terminal terminal) {
                if (firstCall) {
                    firstCall = false;
                    return new WeatherForecast(WeatherSeverity.HIGH);
                }
                return new WeatherForecast(WeatherSeverity.LOW);
            }
        };

        WeatherDelayService weatherService =
                new WeatherDelayService(stubClient);

        DelayCoordinator coordinator =
                new DelayCoordinator(weatherService);

        PredictionEngine engine =
                new PredictionEngine(
                        new SimulationEngine(),
                        new DeliveryEstimateService(),
                        coordinator
                );

        ArrivalEstimate estimate =
                engine.predictArrivalEstimate(
                        snapshot,
                        shipment.getFinalDestination()
                );

        // Baseline arrival would be 6 hours
        Instant baselineArrival =
                Instant.parse("2026-01-01T06:00:00Z");

        // HIGH severity maps to +4 hours in current logic
        Instant expectedDelayedArrival =
                Instant.parse("2026-01-01T10:00:00Z");

        assertEquals(
                expectedDelayedArrival,
                estimate.getTerminalArrival(),
                "Shipment should complete after delay expires"
        );

        assertTrue(
                estimate.getTerminalArrival().isAfter(baselineArrival),
                "Arrival should be later than baseline due to initial delay"
        );
    }
}