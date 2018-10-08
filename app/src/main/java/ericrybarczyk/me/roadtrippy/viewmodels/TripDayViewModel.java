package ericrybarczyk.me.roadtrippy.viewmodels;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;

import ericrybarczyk.me.roadtrippy.dto.TripDay;

public class TripDayViewModel {

    private String tripDayId;
    private String tripId; // reference to Trip object that contains this TripDay
    private int dayNumber;
    private boolean isDrivingDay;
    private boolean isHighlight;
    private LocalDate tripDayDate;
    private String primaryDescription;
    private String userNotes;
    private ArrayList<TripLocationViewModel> destinations;
    private TripLocationViewModel startLocation;
    private TripLocationViewModel endLocation;
    private boolean isDefaultText;

    public TripDayViewModel() {
        destinations = new ArrayList<>();
    }

    public static TripDayViewModel from(TripDay tripDay) {
        TripDayViewModel viewModel = new TripDayViewModel();

        viewModel.setTripDayId(tripDay.getTripDayId());
        viewModel.setTripId(tripDay.getTripId());
        viewModel.setDayNumber(tripDay.getDayNumber());
        viewModel.setIsDrivingDay(tripDay.getIsDrivingDay());
        viewModel.setIsHighlight(tripDay.getIsHighlight());
        viewModel.setTripDayDate(LocalDate.parse(tripDay.getTripDayDate()));
        viewModel.setPrimaryDescription(tripDay.getPrimaryDescription());
        viewModel.setUserNotes(tripDay.getUserNotes());
        viewModel.setIsDefaultText(tripDay.getIsDefaultText());
        // TODO: finish TripDayViewModel.from() with child objects
//        viewModel.setStartLocation(TripLocationViewModel.from(tripDay.getStartLocation()));
//        viewModel.setEndLocation(TripLocationViewModel.from(tripDay.getEndLocation()));

        return viewModel;
    }

    public TripDay asTripDay() {
        TripDay tripDay = new TripDay();
        tripDay.setTripDayId(this.getTripDayId());
        tripDay.setTripId(this.getTripId());
        tripDay.setDayNumber(this.getDayNumber());
        tripDay.setIsDrivingDay(this.getIsDrivingDay());
        tripDay.setIsHighlight(this.getIsHighlight());
        tripDay.setTripDayDate(this.getTripDayDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        tripDay.setPrimaryDescription(this.getPrimaryDescription());
        tripDay.setUserNotes(this.getUserNotes());
        tripDay.setIsDefaultText(this.getIsDefaultText());
        // TODO: finish asTripDay() with child objects
        return tripDay;
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

    public TripLocationViewModel getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(TripLocationViewModel startLocation) {
        this.startLocation = startLocation;
    }

    public TripLocationViewModel getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(TripLocationViewModel endLocation) {
        this.endLocation = endLocation;
    }

    public boolean getIsDefaultText() {
        return isDefaultText;
    }

    public void setIsDefaultText(boolean defaultText) {
        isDefaultText = defaultText;
    }
}
