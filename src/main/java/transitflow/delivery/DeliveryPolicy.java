package transitflow.delivery;

import java.time.Instant;

/**
 * Defines the rules for converting a predicted terminal arrival time
 * into a customer-facing delivery date.
 *
 * Implementations encapsulate terminal-specific business logic such as
 * inbound cut times, weekend delivery behavior, and holiday handling.
 *
 * This abstraction allows delivery rules to vary by terminal without
 * coupling business logic to simulation or prediction code.
 */
public interface DeliveryPolicy {

    /**
     * Calculates the customer delivery date/time based on
     * a predicted terminal arrival time.
     */
    Instant calculateDeliveryDate(Instant terminalArrivalTime);

}
