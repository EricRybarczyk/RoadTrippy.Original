package ericrybarczyk.me.roadtrippy.dto;


public class TripLocation {

    private double latitude;
    private double longitude;
    private String description;
    private String placeId;

    public TripLocation() {
    }

    public TripLocation(double latitude, double longitude, String description, String placeId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.placeId = placeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
