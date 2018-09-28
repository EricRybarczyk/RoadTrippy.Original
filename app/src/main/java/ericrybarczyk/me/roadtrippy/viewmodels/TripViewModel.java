package ericrybarczyk.me.roadtrippy.viewmodels;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.UUID;

import ericrybarczyk.me.roadtrippy.dto.Trip;


public class TripViewModel extends ViewModel {
    private String tripId;
    private String description;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private LatLng originLatLng;
    private String originDescription;
    private LatLng destinationLatLng;
    private String destinationDescription;
    private boolean includeReturn;
    private ArrayList<TripDayViewModel> tripDays;
    private boolean isEdited; // to help UI know if dates are selected by user or if just new instance defaults

    public TripViewModel() {
        tripId = UUID.randomUUID().toString();
        departureDate = LocalDate.now();
        returnDate = LocalDate.now().plusDays(1);
        tripDays = new ArrayList<>();
    }

    public static TripViewModel from(Trip trip) {
        TripViewModel viewModel = new TripViewModel();

        viewModel.setTripId(trip.getTripId());
        viewModel.setDescription(trip.getDescription());
        viewModel.setDepartureDate(LocalDate.parse(trip.getDepartureDate()));
        viewModel.setReturnDate(LocalDate.parse(trip.getReturnDate()));
        viewModel.setOriginLatLng(new LatLng(trip.getOriginLatitude(), trip.getOriginLongitude()));
        viewModel.setOriginDescription("not saved"); // TODO: save origin description in Trip object
        viewModel.setDestinationLatLng(new LatLng(trip.getDestinationLatitude(), trip.getDestinationLongitude()));
        viewModel.setDestinationDescription("not saved"); // TODO: save destination description in Trip object
        viewModel.setIncludeReturn(trip.getIncludeReturn());

        return viewModel;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        isEdited = true;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
        isEdited = true;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
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

    public ArrayList<TripDayViewModel> getTripDays() {
        return tripDays;
    }

    public void setTripDays(ArrayList<TripDayViewModel> tripDays) {
        this.tripDays = tripDays;
    }

    public boolean isEdited() {
        return isEdited;
    }
}
