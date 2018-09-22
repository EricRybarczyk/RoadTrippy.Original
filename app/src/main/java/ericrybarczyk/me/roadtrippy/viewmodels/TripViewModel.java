package ericrybarczyk.me.roadtrippy.viewmodels;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.GregorianCalendar;
import java.util.UUID;

public class TripViewModel extends ViewModel {
    private String tripId;
    private String userId;
    private String description;
    private GregorianCalendar departureDate;
    private GregorianCalendar returnDate;
    private LatLng originLatLng;
    private String originDescription;
    private LatLng destinationLatLng;
    private String destinationDescription;
    private boolean includeReturn;
    private boolean isEdited; // to help UI know if dates are selected by user or if just new instance defaults

    public TripViewModel() {
        tripId = UUID.randomUUID().toString();
        departureDate = new GregorianCalendar();
        returnDate = new GregorianCalendar();
    }

    public String getTripId() {
        return tripId;
    }
    public String getUserId() {
        return userId;
    }
    public String getDescription() {
        return description;
    }
    public GregorianCalendar getDepartureDate() {
        return departureDate;
    }
    public GregorianCalendar getReturnDate() {
        return returnDate;
    }
    public boolean isEdited() {
        return isEdited;
    }
    public LatLng getDestinationLatLng() {
        return destinationLatLng;
    }
    public LatLng getOriginLatLng() {
        return originLatLng;
    }
    public String getOriginDescription() {
        return originDescription;
    }
    public String getDestinationDescription() {
        return destinationDescription;
    }
    public boolean isIncludeReturn() {
        return includeReturn;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
        isEdited = true;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        isEdited = true;
    }

    public void setDescription(String description) {
        this.description = description;
        isEdited = true;
    }

    public void setDepartureDate(GregorianCalendar departureDate) {
        this.departureDate = departureDate;
        isEdited = true;
    }

    public void setReturnDate(GregorianCalendar returnDate) {
        this.returnDate = returnDate;
        isEdited = true;
    }

    public void setOriginLatLng(LatLng originLatLng) {
        this.originLatLng = originLatLng;
        isEdited = true;
    }

    public void setDestinationLatLng(LatLng destinationLatLng) {
        this.destinationLatLng = destinationLatLng;
        isEdited = true;
    }

    public void setIncludeReturn(boolean includeReturn) {
        this.includeReturn = includeReturn;
        isEdited = true;
    }

    public void setOriginDescription(String originDescription) {
        this.originDescription = originDescription;
    }

    public void setDestinationDescription(String destinationDescription) {
        this.destinationDescription = destinationDescription;
    }
}
