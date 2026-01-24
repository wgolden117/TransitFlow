package transitflow.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import transitflow.delivery.StandardDeliveryPolicy;
import transitflow.domain.route.*;
import transitflow.domain.shipment.Shipment;
import transitflow.transport.truck.TruckTransport;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Seeds in-memory data for local development and API testing.
 */
@Configuration
public class ShipmentDataInitializer {

    @Bean
    CommandLineRunner seedShipments(InMemoryShipmentRepository repository) {
        return args -> {
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

            Shipment shipment = new Shipment("TRACK123", route);

            repository.save(shipment);

            System.out.println("Seeded shipment TRACK123");
        };
    }
}
