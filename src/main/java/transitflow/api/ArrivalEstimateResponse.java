package transitflow.api;

import java.time.Instant;

/**
 * API response for arrival estimate queries.
 */
public class ArrivalEstimateResponse {

    private final String trackingId;
    private final Instant terminalArrival;
    private final Instant customerDelivery;

    public ArrivalEstimateResponse(
            String trackingId,
            Instant terminalArrival,
            Instant customerDelivery
    ) {
        this.trackingId = trackingId;
        this.terminalArrival = terminalArrival;
        this.customerDelivery = customerDelivery;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public Instant getTerminalArrival() {
        return terminalArrival;
    }

    public Instant getCustomerDelivery() {
        return customerDelivery;
    }
}
