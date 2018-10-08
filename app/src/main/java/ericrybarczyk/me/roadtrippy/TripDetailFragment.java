package ericrybarczyk.me.roadtrippy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.dto.TripDay;
import ericrybarczyk.me.roadtrippy.persistence.DatabasePaths;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.util.MapSettings;
import ericrybarczyk.me.roadtrippy.viewmodels.TripDayViewModel;

public class TripDetailFragment extends Fragment {

    public static final String KEY_TRIP_ID = "trip_id_key";
    public static final String KEY_TRIP_DESCRIPTION = "trip_description_key";

    private FragmentNavigationRequestListener fragmentNavigationRequestListener;

    private String tripId;
    private String tripDescriptionForDisplay;
    TripRepository tripRepository;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @BindView(R.id.trip_detail_image) protected ImageView tripImage;
    @BindView(R.id.trip_description) protected TextView tripDescription;
    @BindView(R.id.trip_days_list) protected RecyclerView tripDaysListRecyclerView;

    private static final String TAG = TripDetailFragment.class.getSimpleName();

    public TripDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_TRIP_ID)) {
                tripId = savedInstanceState.getString(KEY_TRIP_ID);
                tripDescriptionForDisplay = savedInstanceState.getString(KEY_TRIP_DESCRIPTION);
            }
        } else if (getArguments() != null) {
            if (getArguments().containsKey(KEY_TRIP_ID)) {
                tripId = getArguments().getString(KEY_TRIP_ID);
                tripDescriptionForDisplay = getArguments().getString(KEY_TRIP_DESCRIPTION);
            }
        }
        if (tripId == null) {
            Log.e(TAG, "tripId is null, must be in getArguments() for this fragment");
            return;
        }

        tripRepository = new TripRepository();

        DatabaseReference reference = tripRepository.getTripDaysList(userId, tripId);

        FirebaseRecyclerOptions<TripDay> options = new FirebaseRecyclerOptions.Builder<TripDay>()
                .setQuery(reference, TripDay.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TripDay, TripDayViewHolder>(options) {

            @NonNull
            @Override
            public TripDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_day_list_item, parent, false);
                return new TripDayViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull TripDayViewHolder holder, int position, @NonNull TripDay model) {

                TripDayViewModel viewModel = TripDayViewModel.from(model); // this.getItem(position)

                String nodeKey = this.getRef(position).getKey();

                holder.dayNumber.setText(String.valueOf(viewModel.getDayNumber()));
                holder.dayPrimaryDescription.setText(viewModel.getPrimaryDescription());
                holder.dayUserNotes.setText(viewModel.getUserNotes());

                holder.setTripDayListClickListener(new TripDayViewHolder.OnTripDayListClickListener() {
                    @Override
                    public void onTripDayListItemClick() {
                        fragmentNavigationRequestListener.onTripDayEditFragmentRequest(
                                FragmentTags.TAG_TRIP_DAY,
                                viewModel.getTripId(),
                                viewModel.getDayNumber(),
                                nodeKey);
                    }
                });

                // TODO: bind icon click listener for maps directions intent
            }
        };

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_trip_detail, container, false);
        ButterKnife.bind(this, rootView);

        tripDescription.setText(tripDescriptionForDisplay);

        File imageDir = getContext().getDir(MapSettings.DESTINATION_MAP_IMAGE_DIRECTORY, Context.MODE_PRIVATE);
        String tripImageFilename = MapSettings.DESTINATION_MAP_MAIN_PREFIX + tripId + MapSettings.DESTINATION_MAP_IMAGE_EXTENSION;
        File mapImage = new File(imageDir, tripImageFilename);
        Picasso.with(getContext())
                .load(mapImage)
                .placeholder(R.drawable.map_placeholder)
                .error(R.drawable.map_placeholder)
                .into(tripImage);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        tripDaysListRecyclerView.setLayoutManager(layoutManager);
        tripDaysListRecyclerView.setHasFixedSize(true);
        tripDaysListRecyclerView.setAdapter(firebaseRecyclerAdapter);

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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_TRIP_ID, tripId);
        outState.putString(KEY_TRIP_DESCRIPTION, tripDescriptionForDisplay);
        super.onSaveInstanceState(outState);
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
