package transitflow.simulation;

import transitflow.domain.delay.DelayEvent;
import transitflow.domain.shipment.Shipment;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the authoritative operational state of the simulation.
 *
 * The simulation state tracks the current simulated time, all
 * active shipments, and any delay events that have occurred.
 *
 * It is mutated only by the simulation engine and serves as the
 * source of truth for prediction snapshots.
 */
public class SimulationState {

    private Instant currentTime;
    private final List<Shipment> activeShipments;
    private final List<DelayEvent> delayEvents;

    public SimulationState(Instant startTime, List<Shipment> shipments) {
        this.currentTime = startTime;
        this.activeShipments = shipments;
        this.delayEvents = new ArrayList<>();
    }

    public Instant getCurrentTime() {
        return currentTime;
    }

    public void advanceTime(Duration duration) {
        this.currentTime = currentTime.plus(duration);
    }

    public List<Shipment> getActiveShipments() {
        return activeShipments;
    }

    /**
     * Records a delay event that occurred during simulation.
     */
    public void addDelayEvent(DelayEvent delayEvent) {
        this.delayEvents.add(delayEvent);
    }

    /**
     * Returns an immutable view of all delay events recorded so far.
     */
    public List<DelayEvent> getDelayEvents() {
        return Collections.unmodifiableList(delayEvents);
    }
}
