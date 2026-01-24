package transitflow.prediction;

import transitflow.simulation.SimulationState;
import transitflow.domain.shipment.Shipment;

import java.util.List;
import java.util.stream.Collectors;

public class PredictionSnapshotFactory {

    public static PredictionSnapshot fromSimulationState(SimulationState state) {
        return new PredictionSnapshot(
                state.getCurrentTime(),
                deepCopyShipments(state.getActiveShipments()),
                List.copyOf(state.getDelayEvents())
        );
    }

    private static List<Shipment> deepCopyShipments(List<Shipment> shipments) {
        return shipments.stream()
                .map(Shipment::copy)
                .collect(Collectors.toList());
    }
}
