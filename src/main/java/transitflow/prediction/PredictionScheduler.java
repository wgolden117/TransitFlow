package transitflow.prediction;

import java.time.Duration;

/**
 * Periodically executes prediction runs.
 *
 * This class is responsible for orchestration only,
 * not prediction logic.
 */
public class PredictionScheduler {

    private final PredictionEngine predictionEngine;

    public PredictionScheduler(PredictionEngine predictionEngine) {
        this.predictionEngine = predictionEngine;
    }

    public void runScheduledPrediction() {
        // TODO: Pull snapshot from simulation
        // TODO: Run prediction
        // TODO: Publish/store results
    }
}
