package transitflow.delivery;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Delivery policy that allows weekend deliveries.
 *
 * If a shipment arrives before the inbound cut time, it is eligible
 * for same-day delivery regardless of the day of week. Otherwise,
 * delivery occurs on the following day.
 *
 * This policy is intended for terminals that operate seven days a week.
 */
public class WeekendDeliveryPolicy implements DeliveryPolicy {

    private final BusinessCalendar calendar;
    private final LocalTime inboundCutTime;

    public WeekendDeliveryPolicy(ZoneId zoneId, LocalTime inboundCutTime) {
        this.calendar = new BusinessCalendar(zoneId);
        this.inboundCutTime = inboundCutTime;
    }

    @Override
    public Instant calculateDeliveryDate(Instant terminalArrivalTime) {
        ZonedDateTime arrival = calendar.toZonedDateTime(terminalArrivalTime);

        if (arrival.toLocalTime().isBefore(inboundCutTime)) {
            return arrival.toInstant();
        }

        return arrival.plusDays(1)
                .with(inboundCutTime)
                .toInstant();
    }
}
