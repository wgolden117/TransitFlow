package transitflow.domain.route;

import java.util.List;
import java.util.UUID;

public class Route {

    private final UUID id;
    private final Terminal origin;
    private final Terminal destination;
    private final List<Segment> segments;

    public Route(Terminal origin, Terminal destination, List<Segment> segments) {
        if (segments == null || segments.isEmpty()) {
            throw new IllegalArgumentException("Route must contain at least one segment");
        }

        this.id = UUID.randomUUID();
        this.origin = origin;
        this.destination = destination;
        this.segments = List.copyOf(segments); // immutable copy
    }

    public UUID getId() {
        return id;
    }

    public Terminal getOrigin() {
        return origin;
    }

    public Terminal getDestination() {
        return destination;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public int segmentCount() {
        return segments.size();
    }
}
