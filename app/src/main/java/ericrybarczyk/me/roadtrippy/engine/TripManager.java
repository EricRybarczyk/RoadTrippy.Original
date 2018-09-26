package ericrybarczyk.me.roadtrippy.engine;


import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;

public class TripManager {

    public Trip buildTrip(TripViewModel tripViewModel, String userId) {
        Trip trip = new Trip();

        trip.setUserId(userId);
        trip.setDescription(tripViewModel.getDescription());

        trip.setOriginLatitude(tripViewModel.getOriginLatLng().latitude);
        trip.setOriginLongitude(tripViewModel.getOriginLatLng().longitude);

        trip.setDestinationLatitude(tripViewModel.getDestinationLatLng().latitude);
        trip.setDestinationLongitude(tripViewModel.getDestinationLatLng().longitude);

        trip.setIncludeReturn(tripViewModel.isIncludeReturn());
        trip.setIsArchived(false);

//        trip.setDepartureDate(tripViewModel.getDepartureDate());
//        trip.setReturnDate(tripViewModel.getReturnDate());
//        trip.setOriginLatLng(tripViewModel.getOriginLatLng());
//        trip.setDestinationLatLng(tripViewModel.getDestinationLatLng());

        // TODO: build the list of trip days based on DepartureDate and ReturnDate (inclusive)


        LocalDateTime start = LocalDateTime.of(2018, 10, 17, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2018, 10, 21, 23,59,59);
        Duration duration = Duration.between(start, end);
        long diff = Math.abs(duration.toDays()) + 1; // plus one to be inclusive of end date




//        trip.setCreateDate(new GregorianCalendar());
//        trip.setModifiedDate(new GregorianCalendar());

        return trip;
    }

    public interface TripSaveRequestListener {
        void onTripSaveRequest();
    }

}
