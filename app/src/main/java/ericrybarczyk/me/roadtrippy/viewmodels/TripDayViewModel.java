package ericrybarczyk.me.roadtrippy.viewmodels;

import android.arch.lifecycle.ViewModel;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;

import ericrybarczyk.me.roadtrippy.dto.TripDay;
import ericrybarczyk.me.roadtrippy.dto.TripLocation;
import ericrybarczyk.me.roadtrippy.persistence.PersistenceFormats;

public class TripDayViewModel extends ViewModel {

    private String tripDayId;
    private String tripDayNodeKey; // key to TripDay object in Firebase (the pushKey for the TripDay)
    private String tripId; // key to Trip object that contains this TripDay
    private String tripNodeKey; // key to Trip object in Firebase (the pushKey for the Trip) that contains this TripDay
    private int dayNumber;
    private boolean isDrivingDay;
    private boolean isHighlight;
    private LocalDate tripDayDate;
    private String primaryDescription;
    private String userNotes;
    private ArrayList<TripLocationViewModel> destinations;
    private boolean isDefaultText;

    public TripDayViewModel() {
        destinations = new ArrayList<>();
    }

    public static TripDayViewModel from(TripDay tripDay) {
        TripDayViewModel viewModel = new TripDayViewModel();

        viewModel.setTripDayId(tripDay.getTripDayId());
        viewModel.setTripDayNodeKey(tripDay.getTripDayNodeKey());
        viewModel.setTripId(tripDay.getTripId());
        viewModel.setTripNodeKey(tripDay.getTripNodeKey());
        viewModel.setDayNumber(tripDay.getDayNumber());
        viewModel.setIsDrivingDay(tripDay.getIsDrivingDay());
        viewModel.setIsHighlight(tripDay.getIsHighlight());
        viewModel.setTripDayDate(LocalDate.parse(tripDay.getTripDayDate()));
        viewModel.setPrimaryDescription(tripDay.getPrimaryDescription());
        viewModel.setUserNotes(tripDay.getUserNotes());
        viewModel.setIsDefaultText(tripDay.getIsDefaultText());
        for (TripLocation loc : tripDay.getDestinations()) {
            viewModel.getDestinations().add(TripLocationViewModel.from(loc));
        }

        return viewModel;
    }

    public void updateFrom(TripDay tripDay) {
        this.setTripDayId(tripDay.getTripDayId());
        this.setTripDayNodeKey(tripDay.getTripDayNodeKey());
        this.setTripId(tripDay.getTripId());
        this.setTripNodeKey(tripDay.getTripNodeKey());
        this.setDayNumber(tripDay.getDayNumber());
        this.setIsDrivingDay(tripDay.getIsDrivingDay());
        this.setIsHighlight(tripDay.getIsHighlight());
        this.setTripDayDate(LocalDate.parse(tripDay.getTripDayDate()));
        this.setPrimaryDescription(tripDay.getPrimaryDescription());
        this.setUserNotes(tripDay.getUserNotes());
        this.setIsDefaultText(tripDay.getIsDefaultText());
        this.setDestinations(new ArrayList<>());
        for (TripLocation loc : tripDay.getDestinations()) {
            this.getDestinations().add(TripLocationViewModel.from(loc));
        }
    }

    public TripDay asTripDay() {
        TripDay tripDay = new TripDay();
        tripDay.setTripDayId(this.getTripDayId());
        tripDay.setTripDayNodeKey(this.getTripDayNodeKey());
        tripDay.setTripNodeKey(this.getTripNodeKey());
        tripDay.setTripId(this.getTripId());
        tripDay.setDayNumber(this.getDayNumber());
        tripDay.setIsDrivingDay(this.getIsDrivingDay());
        tripDay.setIsHighlight(this.getIsHighlight());
        tripDay.setTripDayDate(PersistenceFormats.toDateString(this.getTripDayDate()));
        tripDay.setPrimaryDescription(this.getPrimaryDescription());
        tripDay.setUserNotes(this.getUserNotes());
        tripDay.setIsDefaultText(this.getIsDefaultText());
        for (TripLocationViewModel loc : this.getDestinations()) {
            tripDay.getDestinations().add(loc.asTripLocation());
        }

        return tripDay;
    }

    public String getTripDayId() {
        return tripDayId;
    }

    public void setTripDayId(String tripDayId) {
        this.tripDayId = tripDayId;
    }

    public String getTripDayNodeKey() {
        return tripDayNodeKey;
    }

    public void setTripDayNodeKey(String tripDayNodeKey) {
        this.tripDayNodeKey = tripDayNodeKey;
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

    public LocalDate getTripDayDate() {
        return tripDayDate;
    }

    public void setTripDayDate(LocalDate tripDayDate) {
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

    public ArrayList<TripLocationViewModel> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<TripLocationViewModel> destinations) {
        this.destinations = destinations;
    }

    public boolean getIsDefaultText() {
        return isDefaultText;
    }

    public void setIsDefaultText(boolean defaultText) {
        isDefaultText = defaultText;
    }
}
