package transitflow.domain.shipment;

/**
 * Represents the high-level operational status of a shipment.
 */
public enum ShipmentStatus {
    IN_TRANSIT,
    DELAYED,
    ARRIVED_AT_TERMINAL,
    DELIVERED
}
