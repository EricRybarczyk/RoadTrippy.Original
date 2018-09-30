package ericrybarczyk.me.roadtrippy.engine;


import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.dto.TripDay;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;

public class TripManager {

    public TripManager() {
    }

    public Trip buildTrip(TripViewModel tripViewModel, String userId) {
        Trip trip = new Trip();

        trip.setTripId(tripViewModel.getTripId());
        trip.setUserId(userId);
        trip.setDescription(tripViewModel.getDescription());

        trip.setOriginLatitude(tripViewModel.getOriginLatLng().latitude);
        trip.setOriginLongitude(tripViewModel.getOriginLatLng().longitude);
        trip.setOriginDescription(tripViewModel.getOriginDescription());

        trip.setDestinationLatitude(tripViewModel.getDestinationLatLng().latitude);
        trip.setDestinationLongitude(tripViewModel.getDestinationLatLng().longitude);
        trip.setDestinationDescription(tripViewModel.getDestinationDescription());

        trip.setIncludeReturn(tripViewModel.isIncludeReturn());
        trip.setIsArchived(false);

        trip.setDepartureDate(tripViewModel.getDepartureDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        trip.setReturnDate(tripViewModel.getReturnDate().format(DateTimeFormatter.ISO_LOCAL_DATE));

        trip.setDurationMinutes(tripViewModel.getDurationMinutes());

        String rightNow = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        trip.setCreateDate(rightNow);
        trip.setModifiedDate(rightNow);

        return trip;
    }

    public List<TripDay> buildInitialTripDays(TripViewModel tripViewModel) {
        // build the list of trip days based on DepartureDate and ReturnDate (inclusive)

        LocalDateTime start = LocalDateTime.of(
                tripViewModel.getDepartureDate().getYear(),
                tripViewModel.getDepartureDate().getMonthValue(),
                tripViewModel.getDepartureDate().getDayOfMonth(),
                0, 0, 0);

        LocalDateTime end = LocalDateTime.of(
                tripViewModel.getReturnDate().getYear(),
                tripViewModel.getReturnDate().getMonthValue(),
                tripViewModel.getReturnDate().getDayOfMonth(),
                23,59,59);

        Duration duration = Duration.between(start, end);
        long numberOfTripDays = Math.abs(duration.toDays()) + 1; // add one to make it inclusive of last day

        ArrayList<TripDay> tripDays = new ArrayList<>();

        // zero-based loop adjusts for fact that numberOfTripDays is not inclusive of last day
        for (int day = 0; day < numberOfTripDays; day++) {
            TripDay td = new TripDay();
            td.setTripId(tripViewModel.getTripId());
            td.setDayNumber(day + 1); // 1-based for display purposes
            td.setTripDayDate(tripViewModel.getDepartureDate().plusDays(day).format(DateTimeFormatter.ISO_LOCAL_DATE));
            tripDays.add(td);
        }

        return tripDays;
    }

    public interface TripSaveRequestListener {
        void onTripSaveRequest();
    }

}
