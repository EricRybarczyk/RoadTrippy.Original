package ericrybarczyk.me.roadtrippy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.threeten.bp.format.TextStyle;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;


public class TripListFragment extends Fragment {

    private FragmentNavigationRequestListener fragmentNavigationRequestListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tripsDatabaseReference;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private String userId;

    private static final String TAG = TripListFragment.class.getSimpleName();

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
        String userId;
        if (firebaseUser == null) {
            userId = "anonymous-user"; // TODO: gonna have to rework something to avoid this order-of-events problem - user is null on first load
        } else {
            userId = firebaseUser.getUid();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        tripsDatabaseReference = firebaseDatabase.getReference();
        Query query = tripsDatabaseReference.child("trips/" + userId + "/");

        FirebaseRecyclerOptions<Trip> options = new FirebaseRecyclerOptions.Builder<Trip>()
                .setQuery(query, Trip.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Trip, TripViewHolder>(options) {

            @NonNull
            @Override
            public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_item, parent, false);
                return new TripViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull TripViewHolder holder, int position, @NonNull Trip model) {
                TripViewModel viewModel = TripViewModel.from(this.getItem(position));

                String dateRangeText = viewModel.getDepartureDate().getMonth()
                        .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        + " " + String.valueOf(viewModel.getDepartureDate().getDayOfMonth()); // TODO: finish building the display string, or add to view model itself!! And use string builder.

                holder.tripName.setText(viewModel.getDescription());
                holder.tripDateRange.setText(dateRangeText);
            }

//            @Override
//            public int getItemCount() {
//                return super.getItemCount();
//            }
        };

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false); // getContext(), LinearLayoutManager.VERTICAL, false
        tripListRecyclerView.setLayoutManager(layoutManager);
        //tripListRecyclerView.setHasFixedSize(false);

        tripListRecyclerView.setAdapter(firebaseRecyclerAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
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
    }

//    @Override
//    public void onClick(String tripId) {
//
//    }
}
