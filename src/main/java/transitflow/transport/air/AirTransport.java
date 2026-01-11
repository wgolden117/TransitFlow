package transitflow.transport.air;

import transitflow.transport.TransportMode;
import java.time.Duration;

/**
 * Transport mode implementation representing air transportation.
 *
 * Provides baseline transit behavior for air-based route segments.
 */
public class AirTransport implements TransportMode {

    @Override
    public String getName() {
        return "AIR";
    }

    @Override
    public Duration calculateBaseTransitTime() {
        return Duration.ofHours(2); // placeholder
    }
}
