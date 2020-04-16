package net.synchthia.systera.util;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Date;

public class DateUtil {

    public static Long parseDateString(String dateString) throws IllegalArgumentException {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendMonths().appendSuffix("months")
                .appendWeeks().appendSuffix("w")
                .appendDays().appendSuffix("d")
                .appendHours().appendSuffix("h")
                .appendMinutes().appendSuffix("m")
                .appendSeconds().appendSuffix("s")
                .toFormatter();

        Period p = formatter.parsePeriod(dateString);
        return p.toStandardDuration().getMillis();
    }

    public static Date epochToDate(long time) {
        return new Date(time * 1000);
    }

    public static Date epochMilliToDate(long time) {
        return new Date(time);
    }

    public static long getUnixTime() {
        return System.currentTimeMillis() / 1000L;
    }

    public static long getEpochMilliTime() {
        return System.currentTimeMillis();
    }
}
