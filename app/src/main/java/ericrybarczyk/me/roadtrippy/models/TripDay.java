package ericrybarczyk.me.roadtrippy.models;

import java.util.GregorianCalendar;
import java.util.SortedSet;

public class TripDay {

    // TODO: Comparator/Comparable for use by SortedSet<E> - https://docs.oracle.com/javase/8/docs/api/index.html?java/util/SortedSet.html

    private String tripDayId;
    private String tripId; // reference to Trip object that contains this TripDay
    private GregorianCalendar startDate;
    private GregorianCalendar endDate;
    private String primaryDescription;
    private String secondaryDescription;
    private SortedSet<TripLocation> destinations;
    private String userNotes;
    private TripLocation startLocation;
    private TripLocation endLocation;


}
