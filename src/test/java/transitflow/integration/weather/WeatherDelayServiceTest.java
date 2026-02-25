package transitflow.integration.weather;

import org.junit.jupiter.api.Test;
import transitflow.domain.delay.DelayEvent;
import transitflow.domain.route.Terminal;
import transitflow.simulation.SimulationState;
import transitflow.delivery.StandardDeliveryPolicy;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests automatic weather-based delay injection.
 *
 * <p>Verifies that WeatherDelayService correctly translates
 * forecast severity into DelayEvent instances attached
 * to the simulation state.</p>
 */
class WeatherDelayServiceTest {

    @Test
    void highSeverityAddsFourHourDelay() {
        // Arrange
        WeatherClient client = terminal ->
                new WeatherForecast(WeatherSeverity.HIGH);

        WeatherDelayService service =
                new WeatherDelayService(client);

        Terminal terminal = createTerminal("CHI");

        SimulationState state =
                new SimulationState(
                        Instant.parse("2026-01-01T00:00:00Z"),
                        List.of()
                );

        // Act
        service.applyWeatherDelays(state, terminal);

        // Assert
        assertEquals(1, state.getDelayEvents().size());
        DelayEvent delay = state.getDelayEvents().get(0);
        assertEquals(Duration.ofHours(4), delay.getDuration());
        assertEquals("CHI", delay.getLocationId().orElse(null));
    }

    private Terminal createTerminal(String code) {
        return new Terminal(
                code,
                "Test Terminal",
                new StandardDeliveryPolicy(
                        ZoneId.of("America/Chicago"),
                        LocalTime.of(6, 0)
                )
        );
    }

    /**
     * Verifies that LOW severity forecasts do not generate
     * any delay events in the simulation state.
     *
     * <p>This ensures minor weather conditions do not
     * artificially inflate ETA predictions.</p>
     */
    @Test
    void lowSeverityAddsNoDelay() {
        WeatherClient client = terminal ->
                new WeatherForecast(WeatherSeverity.LOW);

        WeatherDelayService service =
                new WeatherDelayService(client);

        Terminal terminal = createTerminal("CHI");

        SimulationState state =
                new SimulationState(
                        Instant.parse("2026-01-01T00:00:00Z"),
                        List.of()
                );

        service.applyWeatherDelays(state, terminal);

        assertTrue(state.getDelayEvents().isEmpty());
    }

    /**
     * Verifies that MODERATE severity forecasts generate
     * a one-hour delay event.
     *
     * <p>This models minor operational slowdowns due to
     * manageable weather disruptions.</p>
     */
    @Test
    void moderateSeverityAddsOneHourDelay() {
        WeatherClient client = terminal ->
                new WeatherForecast(WeatherSeverity.MODERATE);

        WeatherDelayService service =
                new WeatherDelayService(client);

        Terminal terminal = createTerminal("CHI");

        SimulationState state =
                new SimulationState(
                        Instant.parse("2026-01-01T00:00:00Z"),
                        List.of()
                );

        service.applyWeatherDelays(state, terminal);

        assertEquals(1, state.getDelayEvents().size());
        assertEquals(Duration.ofHours(1),
                state.getDelayEvents().get(0).getDuration());
    }

    /**
     * Verifies that EXTREME severity forecasts generate
     * an eight-hour delay event.
     *
     * <p>This ensures severe weather conditions (e.g., hurricanes,
     * blizzards, or major storm systems) significantly impact
     * simulation timing and ETA calculations.</p>
     */
    @Test
    void extremeSeverityAddsEightHourDelay() {
        WeatherClient client = terminal ->
                new WeatherForecast(WeatherSeverity.EXTREME);

        WeatherDelayService service =
                new WeatherDelayService(client);

        Terminal terminal = createTerminal("CHI");

        SimulationState state =
                new SimulationState(
                        Instant.parse("2026-01-01T00:00:00Z"),
                        List.of()
                );

        service.applyWeatherDelays(state, terminal);

        assertEquals(1, state.getDelayEvents().size());
        assertEquals(Duration.ofHours(8),
                state.getDelayEvents().get(0).getDuration());
    }
}