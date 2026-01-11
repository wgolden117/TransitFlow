package transitflow.transport.sea;

import transitflow.transport.TransportMode;

import java.time.Duration;

public class SeaTransport implements TransportMode {

    @Override
    public String getName() {
        return "SEA";
    }

    @Override
    public Duration calculateBaseTransitTime() {
        return Duration.ofDays(3); // placeholder
    }
}
