package ericrybarczyk.me.roadtrippy.persistence;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.dto.TripDay;

public class TripRepository {

    private FirebaseDatabase firebaseDatabase;
    private static final String TAG = TripRepository.class.getSimpleName();

    public TripRepository() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }


    public void saveTrip(Trip trip, List<TripDay> tripDays) {
        try {
            DatabaseReference tripsDatabaseReference = firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_TRIPS + trip.getUserId());

            // get the pushId to use in path when storing the child TripDay records
            String tripPushId = tripsDatabaseReference.push().getKey();

            // save the Trip object under the pushId
            tripsDatabaseReference.child(tripPushId).setValue(trip);

            // build a path for the TripDay child objects so they can be associated with the saved Trip object
            DatabaseReference daysDatabaseReference = firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_TRIPDAYS + trip.getUserId() + "/" + trip.getTripId()); //tripPushId

            // save all TripDay objects
            for (TripDay day : tripDays) {
                daysDatabaseReference.push().setValue(day);
            }

        } catch (Exception e) {
            Log.e(TAG, "Firebase Exception: " + e.getMessage());
            throw e;
        }
    }

    public DatabaseReference getTripList(String userId) {
        return firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_TRIPS + userId + "/");
    }

    public DatabaseReference getTripDaysList(String userId, String tripId) {
        return firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_TRIPDAYS + userId + "/" + tripId);
    }

    public DatabaseReference getTripDay(String userId, String tripId, String dayKey) {
        return firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_TRIPDAYS + userId + "/" + tripId + "/" + dayKey);
    }

    public void updateTripDayHighlight(String userId, String tripId, String dayKey, boolean isHighlight) {
        DatabaseReference reference = getTripDay(userId, tripId, dayKey);

    }
}
