package transitflow.domain.delay;

import java.util.UUID;
import transitflow.transport.TransportMode;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class DelayEvent {

    private final DelayType type;
    private final Duration duration;
    private final Instant occurredAt;
    private final Optional<String> locationId;
    private final String description;

    // optional targeting
    private final Optional<TransportMode> transportMode;
    private final Optional<UUID> segmentId;

    public DelayEvent(
            DelayType type,
            Duration duration,
            Instant occurredAt,
            String locationId,
            String description,
            TransportMode transportMode,
            UUID segmentId
    ) {
        this.type = Objects.requireNonNull(type);
        this.duration = Objects.requireNonNull(duration);
        this.occurredAt = Objects.requireNonNull(occurredAt);
        this.locationId = Optional.ofNullable(locationId);
        this.description = description;

        this.transportMode = Optional.ofNullable(transportMode);
        this.segmentId = Optional.ofNullable(segmentId);
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

    public Optional<String> getLocationId() {
        return locationId;
    }

    public String getDescription() {
        return description;
    }

    public Optional<TransportMode> getTransportMode() {
        return transportMode;
    }

    public Optional<UUID> getSegmentId() {
        return segmentId;
    }
}