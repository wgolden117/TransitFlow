package transitflow.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transitflow.prediction.*;
import transitflow.simulation.SimulationState;

import java.time.Instant;
import java.util.List;

/**
 * REST API for shipment prediction queries.
 */
@RestController
@RequestMapping("/api/shipments")
public class ShipmentPredictionController {

    private final PredictionEngine predictionEngine;
    private final InMemoryShipmentRepository shipmentRepository;

    public ShipmentPredictionController(
            InMemoryShipmentRepository shipmentRepository,
            PredictionEngine predictionEngine
    ) {
        this.shipmentRepository = shipmentRepository;
        this.predictionEngine = predictionEngine;
    }

    @GetMapping("/{trackingId}/arrival-estimate")
    public ResponseEntity<ArrivalEstimateResponse> getArrivalEstimate(
            @PathVariable String trackingId
    ) {
        return shipmentRepository.findByTrackingId(trackingId)
                .map(shipment -> {

                    SimulationState liveState = new SimulationState(
                            Instant.now(),
                            List.of(shipment)
                    );

                    PredictionSnapshot snapshot =
                            PredictionSnapshotFactory.fromSimulationState(liveState);

                    ArrivalEstimate estimate =
                            predictionEngine.predictArrivalEstimate(
                                    snapshot,
                                    shipment.getFinalDestination()
                            );

                    return ResponseEntity.ok(
                            new ArrivalEstimateResponse(
                                    shipment.getTrackingNumber(),
                                    estimate.getTerminalArrival(),
                                    estimate.getCustomerDelivery()
                            )
                    );
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
