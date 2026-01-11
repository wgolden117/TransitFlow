package transitflow.simulation;

import transitflow.domain.shipment.Shipment;

import java.time.Instant;
import java.util.List;

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
