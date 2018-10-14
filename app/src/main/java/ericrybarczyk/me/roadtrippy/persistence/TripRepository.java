package ericrybarczyk.me.roadtrippy.persistence;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.dto.TripDay;
import ericrybarczyk.me.roadtrippy.dto.TripLocation;
import ericrybarczyk.me.roadtrippy.dto.User;

public class TripRepository {

    private FirebaseDatabase firebaseDatabase;
    private static final String TAG = TripRepository.class.getSimpleName();

    public TripRepository() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }


    public void saveTrip(Trip trip, List<TripDay> tripDays) {
        try {
            DatabaseReference tripsDatabaseReference = firebaseDatabase.getReference()
                    .child(DatabasePaths.BASE_PATH_TRIPS + trip.getUserId());

            // get the pushId to use in path when storing the child TripDay records
            String tripPushId = tripsDatabaseReference.push().getKey();

            // save the Trip object under the pushId
            tripsDatabaseReference.child(tripPushId).setValue(trip);

            // build a path for the TripDay child objects so they can be associated with the saved Trip object
            DatabaseReference daysDatabaseReference = firebaseDatabase.getReference()
                    .child(DatabasePaths.BASE_PATH_TRIPDAYS + trip.getUserId() + "/" + trip.getTripId());

            // save all TripDay objects
            for (TripDay day : tripDays) {
                day.setTripNodeKey(tripPushId);
                daysDatabaseReference.push().setValue(day);
            }

        } catch (Exception e) {
            Log.e(TAG, "Firebase Exception: " + e.getMessage());
            throw e;
        }
    }

    public DatabaseReference getTripList(String userId) {
        DatabaseReference reference = firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_TRIPS + userId + "/");
        reference.orderByChild(DatabasePaths.KEY_TRIP_LIST_DEFAULT_SORT);
        return reference;
    }

    public DatabaseReference getArchivedTripList(String userId) {
        DatabaseReference reference = firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_ARCHIVE + userId + "/");
        reference.orderByChild(DatabasePaths.KEY_TRIP_LIST_DEFAULT_SORT);
        return reference;
    }

    public DatabaseReference getTripDaysList(String userId, String tripId) {
        return firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_TRIPDAYS + userId + "/" + tripId);
    }

    public DatabaseReference getTripDay(String userId, String tripId, String dayNodeKey) {
        return firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_TRIPDAYS + userId + "/" + tripId + "/" + dayNodeKey);
    }

    public DatabaseReference getTrip(String userId, String tripNodeKey) {
        return firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_TRIPS + userId + "/" + tripNodeKey);
    }

    public DatabaseReference getDestinationsForTripDay(String userId, String tripId, String dayNodeKey) {
        return firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_TRIPDAYS + userId + "/" + tripId + "/"
                + dayNodeKey + "/" + DatabasePaths.KEY_TRIPDAY_DESTINATIONS_CHILD);
    }

    public void updateTripDayHighlight(String userId, String tripId, String dayNodeKey, boolean isHighlight) {
        DatabaseReference reference = getTripDay(userId, tripId, dayNodeKey);
        reference.child(DatabasePaths.KEY_TRIPDAY_HIGHLIGHT_CHILD).setValue(isHighlight);
    }

    public void updateTripDay(String userId, String tripId, String dayNodeKey, TripDay tripDay) {
        DatabaseReference reference = getTripDay(userId, tripId, dayNodeKey);
        reference.setValue(tripDay);
    }

    public void archiveFinishedTrips(String userId) {
        DatabaseReference tripListReference = getTripList(userId);
        tripListReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        Trip trip = item.getValue(Trip.class);
                        String tripNodeKey = item.getKey();
                        if (trip != null) {
                            // if trip ended yesterday or older, then archive it
                            if ((LocalDate.parse(trip.getReturnDate())).compareTo(LocalDate.now()) < 0) {
                                archiveTrip(userId, tripNodeKey, trip);
                                deleteActiveTrip(userId, tripNodeKey);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void archiveTrip(String userId, String tripNodeKey, Trip trip) {
        DatabaseReference reference = firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_ARCHIVE + userId);
        trip.setIsArchived(true);
        reference.child(tripNodeKey).setValue(trip);
    }

    public void archiveTrip(String userId, String tripNodeKey) {
        DatabaseReference reference = getTrip(userId, tripNodeKey);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                if (trip != null) {
                    archiveTrip(userId, tripNodeKey, trip);
                    deleteActiveTrip(userId, tripNodeKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void deleteActiveTrip(String userId, String tripNodeKey) {
        DatabaseReference reference = getTrip(userId, tripNodeKey);
        reference.removeValue();
    }

    public void removeTripDayDestination(String userId, String tripId, String dayNodeKey, int destinationIndex) {
        // to maintain the destinations with expected array-like indexing, need to re-save the list after item removal.
        // Using ArrayList to avoid manually re-indexing.
        DatabaseReference reference = getTripDay(userId, tripId, dayNodeKey)
                .child(DatabasePaths.KEY_TRIPDAY_DESTINATIONS_CHILD); // + "/" + destinationIndex);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<TripLocation> destinations = new ArrayList<>();
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    destinations.add(item.getValue(TripLocation.class));
                }
                destinations.remove(destinationIndex);
                reference.setValue(destinations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    public void saveUserInfo(FirebaseUser firebaseUser) {
        DatabaseReference reference = firebaseDatabase.getReference().child(DatabasePaths.BASE_PATH_USERS + firebaseUser.getUid());

        // see if user already exists
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String now = PersistenceFormats.toDateTimeString(LocalDateTime.now());
                User existingUser = dataSnapshot.getValue(User.class);
                User user;
                if (existingUser != null) {
                    // update existing user
                    user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), existingUser.getCreateDate(), now);
                } else {
                    // save new user
                    user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), now, now);
                }
                Task<Void> result = reference.setValue(user);
                Log.i(TAG, "save user operation success = " + String.valueOf(result.isSuccessful()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }
}
