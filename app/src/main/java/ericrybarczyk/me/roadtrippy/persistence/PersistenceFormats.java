package ericrybarczyk.me.roadtrippy.persistence;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class PersistenceFormats {

    public static String toDateString(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
