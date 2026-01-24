package transitflow.prediction;

import transitflow.domain.delay.DelayEvent;
import transitflow.domain.shipment.Shipment;

import java.time.Instant;
import java.util.List;

/**
 * Immutable snapshot of simulation state used for prediction.
 */
public class PredictionSnapshot {

    private final Instant snapshotTime;
    private final List<Shipment> shipments;
    private final List<DelayEvent> delayEvents;

    public PredictionSnapshot(
            Instant snapshotTime,
            List<Shipment> shipments,
            List<DelayEvent> delayEvents
    ) {
        this.snapshotTime = snapshotTime;
        this.shipments = shipments;
        this.delayEvents = delayEvents;
    }

    public Instant getSnapshotTime() {
        return snapshotTime;
    }

    public List<Shipment> getShipments() {
        return shipments;
    }

    public List<DelayEvent> getDelayEvents() {
        return delayEvents;
    }
}
