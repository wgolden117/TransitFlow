package transitflow.transport.truck;

import transitflow.transport.TransportMode;

import java.time.Duration;

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
