package transitflow.transport.truck;

import transitflow.transport.TransportMode;

import java.time.Duration;

/**
 * Transport mode implementation representing truck transportation.
 *
 * Provides baseline transit behavior for truck-based route segments.
 */
public class TruckTransport implements TransportMode {

    @Override
    public String getName() {
        return "TRUCK";
    }

    @Override
    public Duration calculateBaseTransitTime() {
        return Duration.ofHours(6); // placeholder
    }
}
