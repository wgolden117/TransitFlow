package transitflow.transport.rail;

import transitflow.transport.TransportMode;

import java.time.Duration;

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
