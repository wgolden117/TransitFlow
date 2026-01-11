package transitflow.delivery;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
