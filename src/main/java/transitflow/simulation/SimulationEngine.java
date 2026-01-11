package transitflow.simulation;

import transitflow.domain.route.Segment;
import transitflow.domain.shipment.Shipment;
import transitflow.domain.shipment.ShipmentStatus;

import java.time.Duration;

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
