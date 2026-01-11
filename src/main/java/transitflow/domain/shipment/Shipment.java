package transitflow.domain.shipment;

import transitflow.domain.route.Route;
import transitflow.domain.route.Segment;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a shipment moving through a multi-segment route.
 * The shipment advances through segments over simulated time
 * and maintains state about its progress and status.
 */
public class Shipment {

    private final UUID id;
    private final String trackingNumber;
    private final Route route;

    private int currentSegmentIndex;
    private Duration remainingSegmentTime;

    private Instant predictedArrivalTime;
    private ShipmentStatus status;

    public Shipment(String trackingNumber, Route route) {
        this.id = UUID.randomUUID();
        this.trackingNumber = trackingNumber;
        this.route = route;

        this.currentSegmentIndex = 0;
        this.remainingSegmentTime = route.getSegments()
                .get(0)
                .getBaseTransitTime();

        this.status = ShipmentStatus.IN_TRANSIT;
    }

    /**
     * Advances this shipment by the given simulated duration.
     * Progresses through one or more segments if time allows.
     */
    public void advance(Duration tick) {
        if (status != ShipmentStatus.IN_TRANSIT) {
            return;
        }

        Duration remainingTick = tick;

        while (remainingTick.isPositive() && hasMoreSegments()) {

            if (remainingTick.compareTo(remainingSegmentTime) >= 0) {
                // Finish current segment
                remainingTick = remainingTick.minus(remainingSegmentTime);
                advanceToNextSegment();
            } else {
                // Partial progress on current segment
                remainingSegmentTime = remainingSegmentTime.minus(remainingTick);
                remainingTick = Duration.ZERO;
            }
        }

        if (!hasMoreSegments()) {
            status = ShipmentStatus.ARRIVED_AT_TERMINAL;
        }
    }

    private void advanceToNextSegment() {
        currentSegmentIndex++;

        if (hasMoreSegments()) {
            Segment nextSegment = route.getSegments().get(currentSegmentIndex);
            remainingSegmentTime = nextSegment.getBaseTransitTime();
        } else {
            remainingSegmentTime = Duration.ZERO;
        }
    }

    public boolean hasMoreSegments() {
        return currentSegmentIndex < route.segmentCount();
    }

    public UUID getId() {
        return id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Route getRoute() {
        return route;
    }

    public int getCurrentSegmentIndex() {
        return currentSegmentIndex;
    }

    public Duration getRemainingSegmentTime() {
        return remainingSegmentTime;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public Instant getPredictedArrivalTime() {
        return predictedArrivalTime;
    }

    public void setPredictedArrivalTime(Instant predictedArrivalTime) {
        this.predictedArrivalTime = predictedArrivalTime;
    }

    public void markDelivered() {
        this.status = ShipmentStatus.DELIVERED;
    }
}


