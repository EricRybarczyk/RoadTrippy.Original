package ericrybarczyk.me.roadtrippy.viewmodels;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.Locale;
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
    private int durationMinutes;
    private boolean includeReturn;
    private boolean isEdited; // to help UI know if values are selected by user or just new instance defaults

    public TripViewModel() {
        init();
    }

    private void init() {
        tripId = UUID.randomUUID().toString();
        description = "";
        originDescription = "";
        destinationDescription = "";
        departureDate = LocalDate.now();
        returnDate = LocalDate.now().plusDays(1);
        includeReturn = true;
        durationMinutes = 0;
        if (isEdited) {
            originLatLng = null;
            destinationLatLng = null;
            isEdited = false;
        }
    }

    public static TripViewModel from(Trip trip) {
        TripViewModel viewModel = new TripViewModel();

        viewModel.setTripId(trip.getTripId());
        viewModel.setDescription(trip.getDescription());
        viewModel.setDepartureDate(LocalDate.parse(trip.getDepartureDate()));
        viewModel.setReturnDate(LocalDate.parse(trip.getReturnDate()));
        viewModel.setOriginLatLng(new LatLng(trip.getOriginLatitude(), trip.getOriginLongitude()));
        viewModel.setOriginDescription(trip.getOriginDescription());
        viewModel.setDestinationLatLng(new LatLng(trip.getDestinationLatitude(), trip.getDestinationLongitude()));
        viewModel.setDestinationDescription(trip.getDestinationDescription());
        viewModel.setIncludeReturn(trip.getIncludeReturn());
        viewModel.setDurationMinutes(trip.getDurationMinutes());

        return viewModel;
    }

    public void updateFrom(Trip trip) {
        this.setTripId(trip.getTripId());
        this.setDescription(trip.getDescription());
        this.setDepartureDate(LocalDate.parse(trip.getDepartureDate()));
        this.setReturnDate(LocalDate.parse(trip.getReturnDate()));
        this.setOriginLatLng(new LatLng(trip.getOriginLatitude(), trip.getOriginLongitude()));
        this.setDestinationLatLng(new LatLng(trip.getDestinationLatitude(), trip.getDestinationLongitude()));
        this.setOriginDescription(trip.getOriginDescription());
        this.setDestinationDescription(trip.getDestinationDescription());
        this.setIncludeReturn(trip.getIncludeReturn());
        this.setDurationMinutes(trip.getDurationMinutes());
    }

    public void reset() {
        init();
    }

    public String getOriginDestinationSummaryText(String joinWord) {

        return originDescription + " "
                + joinWord + " "
                + destinationDescription;
    }

    public String getDateRangeSummaryText(String joinWord) {
        if (departureDate.getMonthValue() == returnDate.getMonthValue()) {
            return departureDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " "
                    + String.valueOf(departureDate.getDayOfMonth()) + " "
                    + joinWord + " "
                    + String.valueOf(returnDate.getDayOfMonth());
        } else {
            return departureDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " "
                    + String.valueOf(departureDate.getDayOfMonth()) + " "
                    + joinWord + " "
                    + returnDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " "
                    + String.valueOf(returnDate.getDayOfMonth());
        }
    }

    public String getDurationDescription(String hours, String minutes, String h, String m, String unknown) {
        if (durationMinutes == 0) {
            return unknown;
        } else if (durationMinutes < 60) {
            return String.valueOf(durationMinutes) + " " + minutes;
        } else if (durationMinutes % 60 == 0) {
            int numHours = durationMinutes / 60;
            return String.valueOf(numHours) + " " + hours;
        } else {
            int numHours = durationMinutes / 60;
            int numMinutes = durationMinutes - (numHours * 60);
            return String.valueOf(numHours) + h + " " + String.valueOf(numMinutes) + m;
        }

    }

    public int getDaysUntilDeparture() {

        LocalDateTime now = LocalDateTime.of(
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonthValue(),
                LocalDateTime.now().getDayOfMonth(),
                0, 0, 0);

        LocalDateTime departure = LocalDateTime.of(
                this.getDepartureDate().getYear(),
                this.getDepartureDate().getMonthValue(),
                this.getDepartureDate().getDayOfMonth(),
                0,0,0);

        Duration duration = Duration.between(now, departure);
        int daysToGo = (int)duration.toDays(); // add one to make it inclusive of last day
        if (daysToGo < 0) {
            return 0;
        }
        return daysToGo;
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

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
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
