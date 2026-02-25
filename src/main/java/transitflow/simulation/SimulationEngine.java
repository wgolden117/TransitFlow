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
            if (hasActiveDelay(state)) {
                // Shipment is blocked by delay; time still advances
                continue;
            }

            shipment.advance(tickSize);
        }
    }

    /**
     * Determines whether there is any active delay in the system.
     */
    private boolean hasActiveDelay(SimulationState state) {
        Instant now = state.getCurrentTime();

        for (DelayEvent delay : state.getDelayEvents()) {
            if (!delayExpired(delay, now)) {
                return true;
            }
        }
        return false;
    }

    private boolean delayExpired(DelayEvent delay, Instant now) {
        return delay.getOccurredAt()
                .plus(delay.getDuration())
                .isBefore(now);
    }
}
