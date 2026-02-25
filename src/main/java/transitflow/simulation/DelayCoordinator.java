package transitflow.simulation;

import org.springframework.stereotype.Component;
import transitflow.domain.route.Terminal;
import transitflow.domain.shipment.Shipment;
import transitflow.integration.weather.WeatherDelayService;

@Component
public class DelayCoordinator {

    private final WeatherDelayService weatherDelayService;

    public DelayCoordinator(WeatherDelayService weatherDelayService) {
        this.weatherDelayService = weatherDelayService;
    }

    public void applyExternalDelays(SimulationState state) {

        for (Shipment shipment : state.getActiveShipments()) {

            if (!shipment.hasMoreSegments()) {
                continue;
            }

            Terminal destination =
                    shipment.getCurrentSegment().getDestination();

            weatherDelayService.applyWeatherDelays(state, destination);
        }
    }
}