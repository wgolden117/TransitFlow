package transitflow.integration.weather;

import transitflow.domain.route.Terminal;

public interface WeatherClient {

    WeatherForecast getForecast(Terminal terminal);

}