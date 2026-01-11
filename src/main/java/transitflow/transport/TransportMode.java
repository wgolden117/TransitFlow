package transitflow.transport;

import java.time.Duration;

/**
 * Represents a mode of transportation used for a route segment,
 * such as truck, rail, air, or sea.
 *
 * Transport modes encapsulate baseline transit behavior and serve
 * as extension points for future enhancements such as weather,
 * congestion, or capacity effects.
 */
public interface TransportMode {

    String getName();

    /**
     * Base transit time for this mode over a given segment.
     * (Later we can factor in distance, congestion, weather, etc.)
     */
    Duration calculateBaseTransitTime();
}
