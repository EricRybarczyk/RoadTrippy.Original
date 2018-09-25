package ericrybarczyk.me.roadtrippy.persistence;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ericrybarczyk.me.roadtrippy.dto.Trip;

public class TripRepository {


    private FirebaseDatabase firebaseDatabase;
    private static final String TAG = TripRepository.class.getSimpleName();


    public TripRepository() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }


    public void saveTrip(Trip trip) {
        try {
            DatabaseReference tripsDatabaseReference = firebaseDatabase.getReference().child("trips/" + trip.getUserId());

//            tripsDatabaseReference.push().setValue(trip);
//            String tripPushId = tripsDatabaseReference.getKey();

            // get the pushId to use when storing the child TripDay records
            String tripPushId = tripsDatabaseReference.push().getKey();

            // save the Trip object under the pushId
            tripsDatabaseReference.child(tripPushId).setValue(trip);

            // build a path for the TripDay child objects so they can be associated with the saved Trip object
            DatabaseReference daysDatabaseReference = firebaseDatabase.getReference().child("tripdays/" + trip.getUserId() + "/" + tripPushId);

            // save all TripDay objects
            daysDatabaseReference.push().setValue("Day 1");
            daysDatabaseReference.push().setValue("Day 2");

        } catch (Exception e) {
            Log.e(TAG, "Firebase Exception: " + e.getMessage());
            throw e;
        }
    }
}
