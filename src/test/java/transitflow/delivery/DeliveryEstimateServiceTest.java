package transitflow.delivery;

import org.junit.jupiter.api.Test;
import transitflow.domain.route.Terminal;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests DeliveryEstimateService behavior.
 *
 * Ensures that terminal-specific delivery policies are applied
 * correctly to predicted terminal arrival times.
 */
class DeliveryEstimateServiceTest {

    @Test
    void appliesDeliveryPolicyToTerminalArrivalTime() {
        // Arrange
        DeliveryEstimateService service = new DeliveryEstimateService();

        DeliveryPolicy policy = new StandardDeliveryPolicy(
                ZoneId.of("America/Chicago"),
                LocalTime.of(6, 0) // 6 AM cut-off
        );

        Terminal terminal = new Terminal(
                "CHI",
                "Chicago",
                policy
        );

        // 5 AM Chicago = 11:00 UTC (before cut-off)
        Instant terminalArrival =
                Instant.parse("2026-01-01T11:00:00Z");

        // Act
        Instant deliveryTime =
                service.estimateDeliveryTime(terminalArrival, terminal);

        // Assert
        assertEquals(
                terminalArrival,
                deliveryTime,
                "Delivery time should match arrival when before cut-off"
        );
    }
}
