package ericrybarczyk.me.roadtrippy.models;

import com.google.android.gms.maps.model.LatLng;

public class TripLocation {

    private LatLng locationLatLng;
    private String description;
    private String placeId;

    public TripLocation() {
    }

    public TripLocation(LatLng locationLatLng, String description, String placeId) {
        this.locationLatLng = locationLatLng;
        this.description = description;
        this.placeId = placeId;
    }

    public LatLng getLocationLatLng() {
        return locationLatLng;
    }

    public void setLocationLatLng(LatLng locationLatLng) {
        this.locationLatLng = locationLatLng;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
