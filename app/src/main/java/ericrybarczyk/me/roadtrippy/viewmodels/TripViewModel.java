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

    public void setUserId(String userId) {
        this.userId = userId;
        isEdited = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        isEdited = true;
    }

    public GregorianCalendar getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(GregorianCalendar departureDate) {
        this.departureDate = departureDate;
        isEdited = true;
    }

    public GregorianCalendar getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(GregorianCalendar returnDate) {
        this.returnDate = returnDate;
        isEdited = true;
    }

    public LatLng getOriginLatLng() {
        return originLatLng;
    }

    public void setOriginLatLng(LatLng originLatLng) {
        this.originLatLng = originLatLng;
        isEdited = true;
    }

    public LatLng getDestinationLatLng() {
        return destinationLatLng;
    }

    public void setDestinationLatLng(LatLng destinationLatLng) {
        this.destinationLatLng = destinationLatLng;
        isEdited = true;
    }

    public String getOriginDescription() {
        if (originDescription == null) {
            return "";
        }
        return originDescription;
    }

    public void setOriginDescription(String originDescription) {
        this.originDescription = originDescription;
        isEdited = true;
    }

    public String getDestinationDescription() {
        if (destinationDescription == null) {
            return "";
        }
        return destinationDescription;
    }

    public void setDestinationDescription(String destinationDescription) {
        this.destinationDescription = destinationDescription;
        isEdited = true;
    }

    public boolean isIncludeReturn() {
        return includeReturn;
    }

    public void setIncludeReturn(boolean includeReturn) {
        this.includeReturn = includeReturn;
        isEdited = true;
    }

    public boolean isEdited() {
        return isEdited;
    }
}
