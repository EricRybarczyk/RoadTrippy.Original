package ericrybarczyk.me.roadtrippy.dto;

import java.util.ArrayList;
import java.util.UUID;

public class TripDay {

    private String tripDayId;
    private String tripId; // reference to Trip object that contains this TripDay
    private String startDate;
    private String endDate;
    private String primaryDescription;
    private String secondaryDescription;
    private String userNotes;
    private ArrayList<TripLocation> destinations;
    private TripLocation startLocation;
    private TripLocation endLocation;

    public TripDay() {
        tripDayId = UUID.randomUUID().toString();
        destinations = new ArrayList<>();
    }

    public String getTripDayId() {
        return tripDayId;
    }

    public void setTripDayId(String tripDayId) {
        this.tripDayId = tripDayId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPrimaryDescription() {
        return primaryDescription;
    }

    public void setPrimaryDescription(String primaryDescription) {
        this.primaryDescription = primaryDescription;
    }

    public String getSecondaryDescription() {
        return secondaryDescription;
    }

    public void setSecondaryDescription(String secondaryDescription) {
        this.secondaryDescription = secondaryDescription;
    }

    public String getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }

    public ArrayList<TripLocation> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<TripLocation> destinations) {
        this.destinations = destinations;
    }

    public TripLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(TripLocation startLocation) {
        this.startLocation = startLocation;
    }

    public TripLocation getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(TripLocation endLocation) {
        this.endLocation = endLocation;
    }
}
