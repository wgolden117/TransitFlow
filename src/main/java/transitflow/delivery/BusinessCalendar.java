package transitflow.delivery;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BusinessCalendar {

    private final ZoneId zoneId;

    public BusinessCalendar(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public boolean isBusinessDay(ZonedDateTime dateTime, boolean allowWeekends) {
        DayOfWeek day = dateTime.getDayOfWeek();

        if (allowWeekends) {
            return true;
        }

        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }

    public ZonedDateTime nextBusinessDay(ZonedDateTime dateTime, boolean allowWeekends) {
        ZonedDateTime next = dateTime.plusDays(1);

        while (!isBusinessDay(next, allowWeekends)) {
            next = next.plusDays(1);
        }

        return next;
    }

    public ZonedDateTime toZonedDateTime(Instant instant) {
        return instant.atZone(zoneId);
    }
}
