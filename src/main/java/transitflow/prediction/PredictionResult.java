package transitflow.prediction;

import transitflow.domain.shipment.Shipment;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import transitflow.simulation.SimulationState;

/**
 * Represents the outcome of a predictive simulation run.
 */
public class PredictionResult {

    private final Instant predictedAt;
    private final List<ShipmentPrediction> shipmentPredictions;

    private PredictionResult(
            Instant predictedAt,
            List<ShipmentPrediction> shipmentPredictions
    ) {
        this.predictedAt = predictedAt;
        this.shipmentPredictions = shipmentPredictions;
    }

    public static PredictionResult from(SimulationState snapshot) {
        return new PredictionResult(
                snapshot.getCurrentTime(),
                snapshot.getActiveShipments().stream()
                        .map(shipment -> ShipmentPrediction.from(
                                shipment,
                                snapshot.getCurrentTime()
                        ))
                        .collect(Collectors.toList())

        );
    }

    public Instant getPredictedAt() {
        return predictedAt;
    }

    public List<ShipmentPrediction> getShipmentPredictions() {
        return shipmentPredictions;
    }
}
