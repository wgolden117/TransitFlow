package transitflow.transport;

import transitflow.transport.air.AirTransport;
import transitflow.transport.rail.RailTransport;
import transitflow.transport.sea.SeaTransport;
import transitflow.transport.truck.TruckTransport;

public class TransportModeFactory {

    public static TransportMode create(String mode) {
        return switch (mode.toUpperCase()) {
            case "TRUCK" -> new TruckTransport();
            case "RAIL" -> new RailTransport();
            case "AIR" -> new AirTransport();
            case "SEA" -> new SeaTransport();
            default -> throw new IllegalArgumentException("Unknown transport mode: " + mode);
        };
    }
}
