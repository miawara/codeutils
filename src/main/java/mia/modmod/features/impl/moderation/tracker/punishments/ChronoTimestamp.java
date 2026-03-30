package mia.modmod.features.impl.moderation.tracker.punishments;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.TimeZone;

public class ChronoTimestamp {
    private long timestamp;

    private ChronoTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() { return timestamp; }

    public static ChronoTimestamp ABSOLUTE_from_Timestamp(long timestamp) { return new ChronoTimestamp(timestamp); }
    public static ChronoTimestamp PAST_from_DHMS(int d, int h, int m, int s) {
        return new ChronoTimestamp(
                (System.currentTimeMillis()) - ((
                        (s) + (m * 60L) + (h * 60L * 60L) + (d * 24L * 60 * 60L)
                        ) * 1000L)
        );
    }

    public String PAST_DHMS_string() { return PAST_DHMS_string(false); }

    public String PAST_DHMS_string(boolean expandedForm) {
        Duration duration = Duration.ofSeconds((System.currentTimeMillis() - timestamp) / 1000L);
        long years = duration.toDays() / 365L;
        long days = duration.toDays() % 365L;
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();

        String dateString = (
                (((years > 0) ? years + (expandedForm ? " year" + ((years == 1) ? "" : "s") + ", ": "y") : "")) +
                        (((days > 0) ? days + (expandedForm ? " day" + ((days == 1) ? "" : "s") + ", ": "d") : "")) +
                        ((years > 0) ? "" : ((hours > 0) ? hours + (expandedForm ? " hour" + ((hours == 1) ? "" : "s") + ", ": "h") : "")) +
                        (((years > 0) || (days > 0))? "" : ((minutes > 0) ? minutes + (expandedForm ? " minute" + ((minutes == 1) ? "" : "s") + ", ": "m") : "")) +
                        (((years > 0) || (days > 0)) ? "" : (seconds + (expandedForm ? " second" + ((seconds == 1) ? "" : "s") + ", ": "s")))
        ).stripTrailing();
        if (dateString.endsWith(",")) dateString = dateString.substring(0, dateString.length()-1);
        return dateString;
    }
}
