package ericrybarczyk.me.roadtrippy.viewmodels;

import ericrybarczyk.me.roadtrippy.dto.TripLocation;

public class TripLocationViewModel {

    private double latitude;
    private double longitude;
    private String description;
    private String placeId;

    public TripLocationViewModel() {
    }

    public TripLocationViewModel(double latitude, double longitude, String description, String placeId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.placeId = placeId;
    }

    public static TripLocationViewModel from(TripLocation tripLocation) {
        TripLocationViewModel viewModel = new TripLocationViewModel();

        viewModel.setLatitude(tripLocation.getLatitude());
        viewModel.setLongitude(tripLocation.getLongitude());
        viewModel.setDescription(tripLocation.getDescription());
        viewModel.setPlaceId(tripLocation.getPlaceId());

        return viewModel;
    }

    public TripLocation asTripLocation() {
        TripLocation tripLocation = new TripLocation();
        tripLocation.setLatitude(this.getLatitude());
        tripLocation.setLongitude(this.getLongitude());
        tripLocation.setDescription(this.getDescription());
        tripLocation.setPlaceId(this.getPlaceId());
        return tripLocation;
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
