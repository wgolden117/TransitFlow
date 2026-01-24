package transitflow.prediction;

import java.time.Instant;

/**
 * Represents the predicted arrival semantics for a shipment.
 *
 * Includes both terminal arrival time and customer-facing
 * delivery availability.
 */
public class ArrivalEstimate {

    private final Instant terminalArrival;
    private final Instant customerDelivery;

    public ArrivalEstimate(Instant terminalArrival, Instant customerDelivery) {
        this.terminalArrival = terminalArrival;
        this.customerDelivery = customerDelivery;
    }

    public Instant getTerminalArrival() {
        return terminalArrival;
    }

    public Instant getCustomerDelivery() {
        return customerDelivery;
    }
}
