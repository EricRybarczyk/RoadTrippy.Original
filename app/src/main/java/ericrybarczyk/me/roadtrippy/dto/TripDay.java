package ericrybarczyk.me.roadtrippy.dto;

import java.util.ArrayList;
import java.util.UUID;

public class TripDay {

    private String tripDayId;
    private String tripId; // key to Trip object that contains this TripDay
    private String tripNodeKey; // key to Trip object in Firebase (the pushKey for the Trip) that contains this TripDay
    private int dayNumber;
    private boolean isDrivingDay;
    private boolean isHighlight;
    private String tripDayDate;
    private String primaryDescription;
    private String userNotes;
    private ArrayList<TripLocation> destinations;
    private TripLocation startLocation;
    private TripLocation endLocation;
    private boolean isDefaultText;

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

    public String getTripNodeKey() {
        return tripNodeKey;
    }

    public void setTripNodeKey(String tripNodeKey) {
        this.tripNodeKey = tripNodeKey;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public boolean getIsDrivingDay() {
        return isDrivingDay;
    }

    public void setIsDrivingDay(boolean drivingDay) {
        isDrivingDay = drivingDay;
    }

    public boolean getIsHighlight() {
        return isHighlight;
    }

    public void setIsHighlight(boolean highlight) {
        isHighlight = highlight;
    }

    public String getTripDayDate() {
        return tripDayDate;
    }

    public void setTripDayDate(String tripDayDate) {
        this.tripDayDate = tripDayDate;
    }

    public String getPrimaryDescription() {
        return primaryDescription;
    }

    public void setPrimaryDescription(String primaryDescription) {
        this.primaryDescription = primaryDescription;
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

    public boolean getIsDefaultText() {
        return isDefaultText;
    }

    public void setIsDefaultText(boolean defaultText) {
        isDefaultText = defaultText;
    }
}
