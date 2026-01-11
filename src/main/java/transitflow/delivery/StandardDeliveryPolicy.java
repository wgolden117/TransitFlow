package transitflow.delivery;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Standard delivery policy that enforces weekday-only delivery
 * and an inbound cut time.
 *
 * If a shipment arrives before the inbound cut time on a business day,
 * it is eligible for same-day delivery. Otherwise, delivery occurs on
 * the next business day at the cut time.
 *
 * This policy represents the default behavior for most freight terminals.
 */
public class StandardDeliveryPolicy implements DeliveryPolicy {

    private final BusinessCalendar calendar;
    private final LocalTime inboundCutTime;

    public StandardDeliveryPolicy(ZoneId zoneId, LocalTime inboundCutTime) {
        this.calendar = new BusinessCalendar(zoneId);
        this.inboundCutTime = inboundCutTime;
    }

    @Override
    public Instant calculateDeliveryDate(Instant terminalArrivalTime) {
        ZonedDateTime arrival = calendar.toZonedDateTime(terminalArrivalTime);

        boolean arrivedBeforeCut =
                arrival.toLocalTime().isBefore(inboundCutTime);

        ZonedDateTime deliveryDate;

        if (arrivedBeforeCut && calendar.isBusinessDay(arrival, false)) {
            deliveryDate = arrival;
        } else {
            deliveryDate = calendar.nextBusinessDay(arrival, false)
                    .with(inboundCutTime);
        }

        return deliveryDate.toInstant();
    }
}
