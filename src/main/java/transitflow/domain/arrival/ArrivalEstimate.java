package transitflow.domain.arrival;

import java.time.Instant;

/**
 * Represents a computed arrival time for a shipment.
 */
public class ArrivalEstimate {

    private final ArrivalType type;
    private final Instant time;

    public ArrivalEstimate(ArrivalType type, Instant time) {
        this.type = type;
        this.time = time;
    }

    public ArrivalType getType() {
        return type;
    }

    public Instant getTime() {
        return time;
    }
}
