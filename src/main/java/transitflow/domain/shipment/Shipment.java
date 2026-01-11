package transitflow.domain.shipment;

import transitflow.domain.route.Route;
import transitflow.domain.route.Segment;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a shipment moving through a multi-segment route.
 *
 * The shipment tracks elapsed time within the current segment
 * and only advances when sufficient time has passed.
 */
public class Shipment {

    private final UUID id;
    private final String trackingNumber;
    private final Route route;

    private int currentSegmentIndex;
    private Duration elapsedInCurrentSegment = Duration.ZERO;
    private ShipmentStatus status;

    public Shipment(String trackingNumber, Route route) {
        this.id = UUID.randomUUID();
        this.trackingNumber = trackingNumber;
        this.route = route;
        this.currentSegmentIndex = 0;
        this.status = ShipmentStatus.IN_TRANSIT;
    }

    public void advance(Duration tick) {
        if (!hasMoreSegments()) {
            return;
        }

        Duration remainingTick = tick;

        while (hasMoreSegments() && remainingTick.compareTo(Duration.ZERO) > 0) {
            Segment current = getCurrentSegment();
            Duration remainingSegmentTime =
                    current.getBaseTransitTime().minus(elapsedInCurrentSegment);

            if (remainingTick.compareTo(remainingSegmentTime) >= 0) {
                // Finish segment
                remainingTick = remainingTick.minus(remainingSegmentTime);
                elapsedInCurrentSegment = Duration.ZERO;
                currentSegmentIndex++;
            } else {
                // Partial progress
                elapsedInCurrentSegment = elapsedInCurrentSegment.plus(remainingTick);
                remainingTick = Duration.ZERO;
            }
        }

        if (!hasMoreSegments()) {
            status = ShipmentStatus.ARRIVED_AT_TERMINAL;
        }
    }

    public boolean hasMoreSegments() {
        return currentSegmentIndex < route.segmentCount();
    }

    public Segment getCurrentSegment() {
        return route.getSegments().get(currentSegmentIndex);
    }

    public int getCurrentSegmentIndex() {
        return currentSegmentIndex;
    }

    public ShipmentStatus getStatus() {
        return status;
    }
}


