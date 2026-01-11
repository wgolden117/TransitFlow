package transitflow.transport.rail;

import transitflow.transport.TransportMode;

import java.time.Duration;

/**
 * Transport mode implementation representing rail transportation.
 *
 * Provides baseline transit behavior for rail-based route segments.
 */
public class RailTransport implements TransportMode {

    @Override
    public String getName() {
        return "RAIL";
    }

    @Override
    public Duration calculateBaseTransitTime() {
        return Duration.ofHours(12); // placeholder
    }
}
