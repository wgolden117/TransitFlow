package transitflow.integration.weather;

import org.springframework.stereotype.Component;
import transitflow.domain.route.Terminal;

@Component
public class StubWeatherClient implements WeatherClient {

    @Override
    public WeatherForecast getForecast(Terminal terminal) {

        // For now, always return HIGH severity.
        // We will make this dynamic later.
        return new WeatherForecast(WeatherSeverity.HIGH);
    }
}