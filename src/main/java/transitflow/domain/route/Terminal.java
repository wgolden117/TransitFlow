package transitflow.domain.route;

import transitflow.delivery.DeliveryPolicy;

import java.util.UUID;

/**
 * Represents a physical freight terminal or hub.
 *
 * Each terminal is associated with a delivery policy that governs
 * how customer delivery dates are calculated based on arrival times.
 *
 * Terminals act as both route endpoints and intermodal transfer points.
 */
public class Terminal {

    private final UUID id;
    private final String code;
    private final String name;
    private final DeliveryPolicy deliveryPolicy;

    public Terminal(String code, String name, DeliveryPolicy deliveryPolicy) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.deliveryPolicy = deliveryPolicy;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public DeliveryPolicy getDeliveryPolicy() {
        return deliveryPolicy;
    }
}
