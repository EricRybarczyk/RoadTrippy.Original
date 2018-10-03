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
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.persistence.DatabasePaths;
import ericrybarczyk.me.roadtrippy.util.FontManager;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.util.MapSettings;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;


public class TripListFragment extends Fragment {

    private FragmentNavigationRequestListener fragmentNavigationRequestListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tripsDatabaseReference;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

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
            userId = "anonymous-user"; // TODO: gonna have to rework something to avoid this order-of-events problem - user is null on first load (sometimes?)
        } else {
            userId = firebaseUser.getUid();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        tripsDatabaseReference = firebaseDatabase.getReference();
        Query query = tripsDatabaseReference.child(DatabasePaths.BASE_PATH_TRIPS + userId + "/");

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
                String joinWord = getString(R.string.word_for_TO);
                String hours = getString(R.string.word_for_HOURS);
                String h = getString(R.string.abbreviation_for_HOURS);
                String minutes = getString(R.string.word_for_MINUTES);
                String m = getString(R.string.abbreviation_for_MINUTES);
                String unknown = getString(R.string.word_for_UNKNOWN);

                holder.setTripId(viewModel.getTripId());
                holder.setTripListClickListener(tripId -> fragmentNavigationRequestListener.onFragmentNavigationRequest(FragmentTags.TAG_TRIP_DETAIL, tripId));

                File imageDir = getContext().getDir(MapSettings.DESTINATION_MAP_IMAGE_DIRECTORY, Context.MODE_PRIVATE);
                String tripImageFilename = MapSettings.DESTINATION_MAP_SLICED_PREFIX + viewModel.getTripId() + MapSettings.DESTINATION_MAP_IMAGE_EXTENSION;
                File mapImage = new File(imageDir, tripImageFilename);
                Picasso.with(getContext())
                        .load(mapImage)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.map_placeholder)
                        .error(R.drawable.map_placeholder)
                        .into(holder.tripImage);

                holder.tripName.setText(viewModel.getDescription());
                holder.tripDirectionsOverview.setText(viewModel.getOriginDestinationSummaryText(joinWord));
                holder.tripDateRange.setText(viewModel.getDateRangeSummaryText(joinWord));
                // TODO: these next three need to be real values
                holder.highlightOne.setText(viewModel.getOriginDescription());
                holder.highlightTwo.setText(viewModel.getDestinationDescription());

                String sb = getString(R.string.label_for_DRIVING_TIME) +
                        " " +
                        viewModel.getDurationDescription(hours, minutes, h, m, unknown);

                holder.drivingDuration.setText(sb);

                holder.iconHighlightOne.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME_SOLID));
                holder.iconHighlightTwo.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME_SOLID));
            }
        };

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false); // getContext(), LinearLayoutManager.VERTICAL, false
        tripListRecyclerView.setLayoutManager(layoutManager);
        tripListRecyclerView.setHasFixedSize(true);

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

}
