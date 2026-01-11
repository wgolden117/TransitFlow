package transitflow.simulation;

import transitflow.domain.route.Segment;
import transitflow.domain.shipment.Shipment;
import transitflow.domain.shipment.ShipmentStatus;

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

            if (!shipment.hasMoreSegments()) {
                shipment.markArrivedAtTerminal();
                continue;
            }

            // For now: naive advancement (one segment per tick)
            shipment.advanceToNextSegment();

            if (!shipment.hasMoreSegments()) {
                shipment.markArrivedAtTerminal();
            }
        }
    }
}
