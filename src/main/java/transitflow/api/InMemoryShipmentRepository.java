package transitflow.api;

import org.springframework.stereotype.Component;
import transitflow.domain.shipment.Shipment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Temporary in-memory shipment store for API testing.
 */
@Component
public class InMemoryShipmentRepository {

    private final Map<String, Shipment> shipments = new HashMap<>();

    public Optional<Shipment> findByTrackingId(String trackingId) {
        return Optional.ofNullable(shipments.get(trackingId));
    }

    public void save(Shipment shipment) {
        shipments.put(shipment.getTrackingNumber(), shipment);
    }
}
