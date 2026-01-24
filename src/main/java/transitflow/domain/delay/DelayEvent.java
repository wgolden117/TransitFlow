package transitflow.domain.delay;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public final class DelayEvent {

    private final DelayType type;
    private final Duration duration;
    private final Instant occurredAt;
    private final String locationId;
    private final String description;

    public DelayEvent(
            DelayType type,
            Duration duration,
            Instant occurredAt,
            String locationId,
            String description
    ) {
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.duration = Objects.requireNonNull(duration, "duration must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        this.locationId = Objects.requireNonNull(locationId, "locationId must not be null");
        this.description = description;
    }

    public DelayType getType() {
        return type;
    }

    public Duration getDuration() {
        return duration;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getDescription() {
        return description;
    }
}
