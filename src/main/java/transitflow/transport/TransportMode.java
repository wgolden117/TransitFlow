package transitflow.transport;

import java.time.Duration;

public interface TransportMode {

    String getName();

    /**
     * Base transit time for this mode over a given segment.
     * (Later we can factor in distance, congestion, weather, etc.)
     */
    Duration calculateBaseTransitTime();
}
