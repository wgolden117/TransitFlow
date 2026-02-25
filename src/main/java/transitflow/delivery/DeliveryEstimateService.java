package transitflow.delivery;

import transitflow.domain.route.Terminal;

import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Applies terminal-specific delivery policies to compute
 * customer-facing delivery availability.
 */
@Component
public class DeliveryEstimateService {

    /**
     * Computes the earliest customer delivery time based on
     * terminal arrival and delivery policy.
     */
    public Instant estimateDeliveryTime(
            Instant terminalArrival,
            Terminal terminal
    ) {
        return terminal.getDeliveryPolicy()
                .calculateDeliveryDate(terminalArrival);
    }
}
