package transitflow.integration.weather;

public class WeatherForecast {

    private final WeatherSeverity severity;

    public WeatherForecast(WeatherSeverity severity) {
        this.severity = severity;
    }

    public WeatherSeverity getSeverity() {
        return severity;
    }
}