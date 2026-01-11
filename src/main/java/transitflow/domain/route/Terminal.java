package transitflow.domain.route;

import transitflow.delivery.DeliveryPolicy;

import java.util.UUID;

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
