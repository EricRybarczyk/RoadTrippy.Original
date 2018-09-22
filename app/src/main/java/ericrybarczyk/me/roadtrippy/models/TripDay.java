package ericrybarczyk.me.roadtrippy.models;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class TripDay {

    private String tripDayId;
    private String tripId; // reference to Trip object that contains this TripDay
    private GregorianCalendar startDate;
    private GregorianCalendar endDate;
    private String primaryDescription;
    private String secondaryDescription;
    private ArrayList<TripLocation> destinations;
    private String userNotes;
    private TripLocation startLocation;
    private TripLocation endLocation;

}
