package transitflow.domain.shipment;

import transitflow.domain.route.Route;
import transitflow.domain.route.Segment;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a unit of freight moving through the transportation network.
 *
 * A shipment is associated with a route and maintains its current position
 * within that route, along with status information used by simulation
 * and prediction engines.
 */
public class Shipment {

    private final UUID id;
    private final String trackingNumber;
    private final Route route;

    private int currentSegmentIndex;
    private Instant predictedArrivalTime;
    private ShipmentStatus status;

    public Shipment(String trackingNumber, Route route) {
        this.id = UUID.randomUUID();
        this.trackingNumber = trackingNumber;
        this.route = route;
        this.currentSegmentIndex = 0;
        this.status = ShipmentStatus.IN_TRANSIT;
    }

    public boolean hasMoreSegments() {
        return currentSegmentIndex < route.segmentCount() - 1;
    }

    public void advanceToNextSegment() {
        if (!hasMoreSegments()) {
            throw new IllegalStateException("Shipment has completed all segments");
        }
        currentSegmentIndex++;
    }

    public Segment getCurrentSegment() {
        return route.getSegments().get(currentSegmentIndex);
    }

    public Route getRoute() {
        return route;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void markArrivedAtTerminal() {
        this.status = ShipmentStatus.ARRIVED_AT_TERMINAL;
    }

    public void markDelivered() {
        this.status = ShipmentStatus.DELIVERED;
    }
}

