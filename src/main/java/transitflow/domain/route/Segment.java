package transitflow.domain.route;

import transitflow.transport.TransportMode;

import java.time.Duration;
import java.util.UUID;

public class Segment {

    private final UUID id;
    private final Terminal from;
    private final Terminal to;
    private final TransportMode transportMode;

    public Segment(Terminal from,
                   Terminal to,
                   TransportMode transportMode) {

        this.id = UUID.randomUUID();
        this.from = from;
        this.to = to;
        this.transportMode = transportMode;
    }

    public UUID getId() {
        return id;
    }

    public Terminal getFrom() {
        return from;
    }

    public Terminal getTo() {
        return to;
    }

    public TransportMode getTransportMode() {
        return transportMode;
    }

    public Duration getBaseTransitTime() {
        return transportMode.calculateBaseTransitTime();
    }
}
