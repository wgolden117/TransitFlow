package transitflow.delivery;

import java.time.Instant;

public interface DeliveryPolicy {

    /**
     * Calculates the customer delivery date/time based on
     * a predicted terminal arrival time.
     */
    Instant calculateDeliveryDate(Instant terminalArrivalTime);

}
