package ericrybarczyk.me.roadtrippy.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.UUID;

public class Trip {
    private String tripId;
    private String userId;
    private String description;
    private GregorianCalendar departureDate;
    private GregorianCalendar returnDate;
    private LatLng originLatLng;
    private LatLng destinationLatLng;
    private boolean includeReturn;
    private boolean isArchived;
    private GregorianCalendar createDate;
    private GregorianCalendar modifiedDate;
    private ArrayList<TripDay> tripDays;

    public Trip() {
        tripId = UUID.randomUUID().toString();
        tripDays = new ArrayList<>();
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GregorianCalendar getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(GregorianCalendar departureDate) {
        this.departureDate = departureDate;
    }

    public GregorianCalendar getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(GregorianCalendar returnDate) {
        this.returnDate = returnDate;
    }

    public LatLng getOriginLatLng() {
        return originLatLng;
    }

    public void setOriginLatLng(LatLng originLatLng) {
        this.originLatLng = originLatLng;
    }

    public LatLng getDestinationLatLng() {
        return destinationLatLng;
    }

    public void setDestinationLatLng(LatLng destinationLatLng) {
        this.destinationLatLng = destinationLatLng;
    }

    public boolean isIncludeReturn() {
        return includeReturn;
    }

    public void setIncludeReturn(boolean includeReturn) {
        this.includeReturn = includeReturn;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public GregorianCalendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(GregorianCalendar createDate) {
        this.createDate = createDate;
    }

    public GregorianCalendar getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(GregorianCalendar modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public ArrayList<TripDay> getTripDays() {
        return tripDays;
    }

    public void setTripDays(ArrayList<TripDay> tripDays) {
        this.tripDays = tripDays;
    }
}
