package ericrybarczyk.me.roadtrippy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;


public class TripListFragment extends Fragment implements TripAdapter.TripAdapterOnClickHandler {

    private FragmentNavigationRequestListener fragmentNavigationRequestListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tripsDatabaseReference;
    private ChildEventListener childEventListener;
    private TripAdapter tripAdapter;
    private String userId;

    @BindView(R.id.trip_list) protected RecyclerView tripListRecyclerView;
    @BindView(R.id.fab) protected FloatingActionButton fab;

    public TripListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        ButterKnife.bind(this, rootView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        tripsDatabaseReference = firebaseDatabase.getReference().child("trips/" + firebaseUser.getUid());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        tripListRecyclerView.setLayoutManager(layoutManager);
        tripListRecyclerView.setHasFixedSize(true);

        tripAdapter = new TripAdapter(getContext(), this);
        tripListRecyclerView.setAdapter(tripAdapter);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // fires when new item is inserted, plus gives every item when first attached
                Trip trip = dataSnapshot.getValue(Trip.class);
                tripAdapter.addTrip(TripViewModel.from(trip));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        tripsDatabaseReference.addChildEventListener(childEventListener);

        return rootView;
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        fragmentNavigationRequestListener.onFragmentNavigationRequest(FragmentTags.TAG_CREATE_TRIP);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigationRequestListener) {
            fragmentNavigationRequestListener = (FragmentNavigationRequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentNavigationRequestListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentNavigationRequestListener = null;
        if (childEventListener != null) {
            tripsDatabaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    @Override
    public void onClick(String tripId) {

    }
}
