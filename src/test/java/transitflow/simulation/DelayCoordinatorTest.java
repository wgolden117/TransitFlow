package transitflow.simulation;

import org.junit.jupiter.api.Test;
import transitflow.domain.delay.DelayEvent;
import transitflow.domain.delay.DelayType;
import transitflow.domain.route.*;
import transitflow.domain.shipment.Shipment;
import transitflow.delivery.StandardDeliveryPolicy;
import transitflow.integration.weather.WeatherDelayService;
import transitflow.transport.truck.TruckTransport;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests orchestration behavior of the DelayCoordinator.
 *
 * <p>These tests verify that external delay services are invoked
 * correctly for active shipments and that delay events are
 * injected into simulation state as expected.</p>
 *
 * <p>This ensures separation of concerns between simulation
 * time progression and external signal coordination.</p>
 */
class DelayCoordinatorTest {

    /**
     * Verifies that the coordinator invokes the weather delay
     * service for an active shipment and injects a delay event.
     */
    @Test
    void coordinatorAppliesWeatherDelayForActiveShipment() {

        // Arrange
        Shipment shipment = createSingleSegmentShipment();
        SimulationState state = createStateWithShipment(shipment);

        StubWeatherDelayService stubService =
                new StubWeatherDelayService(Duration.ofHours(2));

        DelayCoordinator coordinator =
                new DelayCoordinator(stubService);

        // Act
        coordinator.applyExternalDelays(state);

        // Assert
        assertEquals(1, state.getDelayEvents().size());
        assertEquals(Duration.ofHours(2),
                state.getDelayEvents().get(0).getDuration());
    }

    /**
     * Verifies that the coordinator does not apply
     * delays for shipments that have completed
     * all route segments.
     */
    @Test
    void coordinatorDoesNotApplyDelayForCompletedShipment() {

        // Arrange
        Shipment shipment = createSingleSegmentShipment();
        SimulationState state = createStateWithShipment(shipment);

        // Complete shipment
        shipment.advance(Duration.ofHours(6));

        StubWeatherDelayService stubService =
                new StubWeatherDelayService(Duration.ofHours(2));

        DelayCoordinator coordinator =
                new DelayCoordinator(stubService);

        // Act
        coordinator.applyExternalDelays(state);

        // Assert
        assertTrue(state.getDelayEvents().isEmpty());
    }

    /**
     * Verifies that the coordinator applies
     * delays independently for multiple
     * active shipments.
     */
    @Test
    void coordinatorAppliesDelaysForMultipleShipments() {

        // Arrange
        Shipment shipment1 = createSingleSegmentShipment();
        Shipment shipment2 = createSingleSegmentShipment();

        SimulationState state =
                new SimulationState(
                        Instant.parse("2026-01-01T00:00:00Z"),
                        List.of(shipment1, shipment2)
                );

        StubWeatherDelayService stubService =
                new StubWeatherDelayService(Duration.ofHours(1));

        DelayCoordinator coordinator =
                new DelayCoordinator(stubService);

        // Act
        coordinator.applyExternalDelays(state);

        // Assert
        assertEquals(2, state.getDelayEvents().size());
    }

    /* ------------------------------------------------------------------
       Test helpers
       ------------------------------------------------------------------ */

    /**
     * Simple stub implementation of WeatherDelayService
     * used for testing orchestration behavior.
     *
     * <p>This stub injects a delay of a fixed duration
     * whenever invoked.</p>
     */
    private static class StubWeatherDelayService
            extends WeatherDelayService {

        private final Duration durationToInject;

        public StubWeatherDelayService(Duration durationToInject) {
            super(null); // WeatherClient not needed for stub
            this.durationToInject = durationToInject;
        }

        @Override
        public void applyWeatherDelays(
                SimulationState state,
                Terminal terminal
        ) {
            state.addDelayEvent(new DelayEvent(
                    DelayType.WEATHER,
                    durationToInject,
                    state.getCurrentTime(),
                    terminal.getCode(),
                    "Stub delay",
                    null,
                    null
            ));
        }
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

    private SimulationState createStateWithShipment(Shipment shipment) {
        return new SimulationState(
                Instant.parse("2026-01-01T00:00:00Z"),
                List.of(shipment)
        );
    }
}