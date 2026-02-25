package transitflow.prediction;

import transitflow.delivery.DeliveryEstimateService;
import transitflow.domain.route.Terminal;
import transitflow.domain.shipment.Shipment;
import transitflow.simulation.DelayCoordinator;
import transitflow.simulation.SimulationEngine;
import transitflow.simulation.SimulationState;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Executes read-only predictive simulations based on
 * immutable prediction snapshots.
 */
@Component
public class PredictionEngine {

    private final SimulationEngine simulationEngine;
    private final DeliveryEstimateService deliveryService;
    private final DelayCoordinator delayCoordinator;

    public PredictionEngine(
            SimulationEngine simulationEngine,
            DeliveryEstimateService deliveryService,
            DelayCoordinator delayCoordinator
    ) {
        this.simulationEngine = simulationEngine;
        this.deliveryService = deliveryService;
        this.delayCoordinator = delayCoordinator;
    }

    /**
     * Runs a predictive simulation forward for the given horizon.
     * This method operates exclusively on prediction snapshots
     * and cannot mutate live simulation state.
     */
    public PredictionResult predict(PredictionSnapshot snapshot, Duration horizon) {

        SimulationState predictedState = new SimulationState(
                snapshot.getSnapshotTime(),
                snapshot.getShipments()
        );

        // Apply external delays before advancing
        delayCoordinator.applyExternalDelays(predictedState);

        simulationEngine.tick(predictedState, horizon);

        return PredictionResult.from(predictedState);
    }

    /**
     * Predicts arrival semantics (terminal arrival + customer delivery).
     * This is the primary API for UI and external consumers.
     */
    public ArrivalEstimate predictArrivalEstimate(
            PredictionSnapshot snapshot,
            Terminal destination
    ) {

        SimulationState state = new SimulationState(
                snapshot.getSnapshotTime(),
                snapshot.getShipments()
        );

        // Advance simulation until all shipments complete
        while (state.getActiveShipments().stream()
                .anyMatch(Shipment::hasMoreSegments)) {

            // Apply external delays at each step
            delayCoordinator.applyExternalDelays(state);

            simulationEngine.tick(state, Duration.ofHours(1));
        }

        Instant terminalArrival = state.getCurrentTime();

        Instant customerDelivery =
                deliveryService.estimateDeliveryTime(
                        terminalArrival,
                        destination
                );

        return new ArrivalEstimate(terminalArrival, customerDelivery);
    }
}
