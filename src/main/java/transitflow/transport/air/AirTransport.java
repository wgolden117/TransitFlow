package transitflow.transport.air;

import transitflow.transport.TransportMode;

import java.time.Duration;

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
