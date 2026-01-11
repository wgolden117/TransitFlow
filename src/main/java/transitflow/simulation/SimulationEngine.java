package transitflow.simulation;

import transitflow.domain.shipment.Shipment;

import java.time.Duration;

/**
 * Advances the operational simulation forward in time.
 *
 * The simulation engine mutates the live simulation state by
 * progressing shipments through their routes based on elapsed
 * simulated time.
 *
 * This engine represents operational truth and is kept separate
 * from predictive forecasting logic.
 */
public class SimulationEngine {

    public void tick(SimulationState state, Duration tickSize) {
        state.advanceTime(tickSize);

        for (Shipment shipment : state.getActiveShipments()) {
            shipment.advance(tickSize);
        }
    }
}
