package transitflow.prediction;

import transitflow.simulation.SimulationEngine;
import transitflow.simulation.SimulationState;

import java.time.Duration;

/**
 * Executes read-only predictive simulations based on
 * immutable prediction snapshots.
 */
public class PredictionEngine {

    private final SimulationEngine simulationEngine = new SimulationEngine();

    /**
     * Runs a predictive simulation forward for the given horizon.
     *
     * This method operates exclusively on prediction snapshots
     * and cannot mutate live simulation state.
     */
    public PredictionResult predict(PredictionSnapshot snapshot, Duration horizon) {

        // Convert snapshot into a mutable simulation state clone
        SimulationState predictedState = new SimulationState(
                snapshot.getSnapshotTime(),
                snapshot.getShipments()
        );

        simulationEngine.tick(predictedState, horizon);

        return PredictionResult.from(predictedState);
    }
}
