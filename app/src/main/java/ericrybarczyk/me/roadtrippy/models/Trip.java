package ericrybarczyk.me.roadtrippy.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.GregorianCalendar;
import java.util.SortedSet;

public class Trip {
    private String tripId;
    private String userId;
    private String description;
    private GregorianCalendar startDate;
    private GregorianCalendar endDate;
    private LatLng originLatLng;
    private LatLng destinationLatLng;
    private boolean includeReturn;
    private boolean isArchived;
    private GregorianCalendar createDate;
    private GregorianCalendar modifiedDate;
    private SortedSet<TripDay> tripDays;
}
