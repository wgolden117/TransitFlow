package transitflow.simulation;

import transitflow.domain.shipment.Shipment;
import java.time.Instant;
import java.util.List;

/**
 * Holds the authoritative operational state of the simulation.
 *
 * The simulation state tracks the current simulated time and all
 * active shipments. It is mutated only by the simulation engine
 * and serves as the source of truth for prediction snapshots.
 */
public class SimulationState {

    private Instant currentTime;
    private final List<Shipment> activeShipments;

    public SimulationState(Instant startTime, List<Shipment> shipments) {
        this.currentTime = startTime;
        this.activeShipments = shipments;
    }

    public Instant getCurrentTime() {
        return currentTime;
    }

    public void advanceTime(java.time.Duration duration) {
        this.currentTime = currentTime.plus(duration);
    }

    public List<Shipment> getActiveShipments() {
        return activeShipments;
    }
}
