package transitflow.integration.weather;

import org.springframework.stereotype.Component;
import transitflow.domain.delay.DelayEvent;
import transitflow.domain.delay.DelayType;
import transitflow.domain.route.Terminal;
import transitflow.simulation.SimulationState;

import java.time.Duration;

@Component
public class WeatherDelayService {

    private final WeatherClient weatherClient;

    public WeatherDelayService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    public void applyWeatherDelays(
            SimulationState state,
            Terminal terminal
    ) {

        WeatherForecast forecast = weatherClient.getForecast(terminal);

        WeatherSeverity severity = forecast.getSeverity();

        Duration delayDuration = mapSeverityToDelay(severity);

        if (!delayDuration.isZero()) {
            state.addDelayEvent(new DelayEvent(
                    DelayType.WEATHER,
                    delayDuration,
                    state.getCurrentTime(),
                    terminal.getCode(),
                    "Weather disruption",
                    null,
                    null
            ));
        }
    }

    private Duration mapSeverityToDelay(WeatherSeverity severity) {
        return switch (severity) {
            case LOW -> Duration.ZERO;
            case MODERATE -> Duration.ofHours(1);
            case HIGH -> Duration.ofHours(4);
            case EXTREME -> Duration.ofHours(8);
        };
    }
}