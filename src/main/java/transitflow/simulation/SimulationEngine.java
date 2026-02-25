package transitflow.simulation;

import transitflow.domain.delay.DelayEvent;
import transitflow.domain.shipment.Shipment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Delay handling:
 * - Active delays block shipment advancement
 * - Simulation time always advances
 * - Delay applicability is currently global
 *   and will be refined in future iterations
 */
@Component
public class SimulationEngine {

    public void tick(SimulationState state, Duration tickSize) {
        state.advanceTime(tickSize);

        for (Shipment shipment : state.getActiveShipments()) {
            if (isShipmentBlockedByDelay(state, shipment)) {
                continue;
            }

            shipment.advance(tickSize);
        }
    }

    private boolean delayExpired(DelayEvent delay, Instant now) {
        return delay.getOccurredAt()
                .plus(delay.getDuration())
                .isBefore(now);
    }

    private boolean isShipmentBlockedByDelay(
            SimulationState state,
            Shipment shipment
    ) {
        Instant now = state.getCurrentTime();

        if (!shipment.hasMoreSegments()) {
            return false;
        }

        var currentSegment = shipment.getCurrentSegment();

        for (DelayEvent delay : state.getDelayEvents()) {

            if (delayExpired(delay, now)) {
                continue;
            }

            // GLOBAL delay
            if (delay.getTransportMode().isEmpty()
                    && delay.getSegmentId().isEmpty()
                    && delay.getLocationId().isEmpty()) {
                return true;
            }

            // Transport mode scoped delay
            if (delay.getTransportMode().isPresent()
                    && currentSegment.getTransportMode()
                    .equals(delay.getTransportMode().get())) {
                return true;
            }

            // Segment scoped delay
            if (delay.getSegmentId().isPresent()
                    && currentSegment.getId()
                    .equals(delay.getSegmentId().get())) {
                return true;
            }

            // Location scoped delay
            if (delay.getLocationId().isPresent()
                    && currentSegment.getDestination()
                    .getCode()
                    .equals(delay.getLocationId().get())) {
                return true;
            }
        }

        return false;
    }
}
