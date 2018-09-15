package ericrybarczyk.me.roadtrippy.util;

import java.text.DateFormat;
import java.util.GregorianCalendar;


public class DateUtils {

    public static String formatDate(GregorianCalendar calendar) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }
}
