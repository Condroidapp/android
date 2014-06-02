package cz.quinix.condroid;

import org.joda.time.DateTimeZone;
import org.joda.time.tz.Provider;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;


public class FastJodaTimeZoneProvider implements Provider {

    public static final Set<String> AVAILABLE_IDS = new HashSet<String>();

    static {
        AVAILABLE_IDS.addAll(Arrays.asList(TimeZone.getAvailableIDs()));
    }

    public DateTimeZone getZone(String id) {
        if (id == null) {
            return DateTimeZone.UTC;
        }

        TimeZone tz = TimeZone.getTimeZone(id);
        if (tz == null) {
            return DateTimeZone.UTC;
        }

        int rawOffset = tz.getRawOffset();

        //sub-optimal. could be improved to only create a new Date every few minutes
        if (tz.inDaylightTime(new Date())) {
            rawOffset += tz.getDSTSavings();
        }

        return DateTimeZone.forOffsetMillis(rawOffset);
    }

    public Set getAvailableIDs() {
        return AVAILABLE_IDS;
    }
}

