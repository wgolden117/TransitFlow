package transitflow.prediction;

import transitflow.domain.shipment.Shipment;

import java.time.Instant;

/**
 * Predicted outcome for a single shipment.
 */
public class ShipmentPrediction {

    private final String shipmentId;
    private final Instant predictedCompletionTime;

    private ShipmentPrediction(String shipmentId, Instant predictedCompletionTime) {
        this.shipmentId = shipmentId;
        this.predictedCompletionTime = predictedCompletionTime;
    }

    public static ShipmentPrediction from(Shipment shipment, Instant snapshotTime) {
        return new ShipmentPrediction(
                shipment.toString(), // temporary identifier
                snapshotTime
        );
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public Instant getPredictedCompletionTime() {
        return predictedCompletionTime;
    }
}
