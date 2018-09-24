package ericrybarczyk.me.roadtrippy.engine;

import java.util.GregorianCalendar;

import ericrybarczyk.me.roadtrippy.models.Trip;
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

//        trip.setCreateDate(new GregorianCalendar());
//        trip.setModifiedDate(new GregorianCalendar());

        return trip;
    }

    public interface TripSaveRequestListener {
        void onTripSaveRequest();
    }

}
