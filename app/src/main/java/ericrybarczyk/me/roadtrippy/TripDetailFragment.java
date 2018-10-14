package ericrybarczyk.me.roadtrippy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.threeten.bp.LocalDate;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.dto.TripDay;
import ericrybarczyk.me.roadtrippy.endpoints.NavigationIntentService;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;
import ericrybarczyk.me.roadtrippy.util.ArgumentKeys;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.util.MapSettings;
import ericrybarczyk.me.roadtrippy.viewmodels.TripDayViewModel;

public class TripDetailFragment extends Fragment {

    private FragmentNavigationRequestListener fragmentNavigationRequestListener;
    private TripDisplayCommunicationListener tripDisplayCommunicationListener;

    private String tripId;
    private String tripNodeKey;
    private String tripDescriptionForDisplay;
    private boolean tripIsArchived = false;
    String userId;
    TripRepository tripRepository;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @BindView(R.id.trip_detail_image) protected ImageView tripImage;
    @BindView(R.id.trip_description) protected TextView tripDescription;
    @BindView(R.id.trip_days_list) protected RecyclerView tripDaysListRecyclerView;

    private static final String TAG = TripDetailFragment.class.getSimpleName();

    public TripDetailFragment() {
        // Required empty public constructor
    }

    // allows fragment to tell MainActivity the lookup key for the current Trip, to support menu actions
    public interface TripDisplayCommunicationListener {
        void tripDisplayCommunication(String tripNodeKey);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ArgumentKeys.KEY_TRIP_ID)) {
                tripId = savedInstanceState.getString(ArgumentKeys.KEY_TRIP_ID);
                tripNodeKey = savedInstanceState.getString(ArgumentKeys.KEY_TRIP_NODE_KEY);
            }
        } else if (getArguments() != null) {
            if (getArguments().containsKey(ArgumentKeys.KEY_TRIP_ID)) {
                tripId = getArguments().getString(ArgumentKeys.KEY_TRIP_ID);
                tripNodeKey = getArguments().getString(ArgumentKeys.KEY_TRIP_NODE_KEY);
            }
            if (getArguments().containsKey(ArgumentKeys.TRIP_IS_ARCHIVED_KEY)) {
                tripIsArchived = getArguments().getBoolean(ArgumentKeys.TRIP_IS_ARCHIVED_KEY);
            }
        }
        if (tripId == null || tripNodeKey == null) {
            Log.e(TAG, "tripId is null, must be in getArguments() for this fragment");
            return;
        }

        // tell parent activity which trip is being displayed, to support menu item actions
        tripDisplayCommunicationListener.tripDisplayCommunication(tripNodeKey);

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

                String dayNodeKey = this.getRef(position).getKey();

                holder.dayNumber.setText(String.valueOf(viewModel.getDayNumber()));
                holder.dayPrimaryDescription.setText(viewModel.getPrimaryDescription());
                holder.dayUserNotes.setText(viewModel.getUserNotes());

                if (isToday(viewModel.getTripDayDate())) {
                    holder.layoutContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                }

                if (tripIsArchived) {
                    holder.setTripDayListClickListener(new TripDayViewHolder.OnTripDayListClickListener() {
                        @Override
                        public void onTripDayListItemClick() {
                            Snackbar.make(tripDaysListRecyclerView, getString(R.string.error_message_archived_trip_no_edit), Snackbar.LENGTH_LONG).show();
                        }
                    });
                } else {
                    holder.setTripDayListClickListener(new TripDayViewHolder.OnTripDayListClickListener() {
                        @Override
                        public void onTripDayListItemClick() {
                            fragmentNavigationRequestListener.onTripDayEditFragmentRequest(
                                    FragmentTags.TAG_TRIP_DAY,
                                    viewModel.getTripId(),
                                    tripNodeKey,
                                    viewModel.getDayNumber(),
                                    dayNodeKey);
                        }
                    });
                }


                if (viewModel.getDestinations().size() == 0) {
                    holder.iconNavigation.setVisibility(View.INVISIBLE);
                } else {
                    holder.setNavigationClickListener(new TripDayViewHolder.OnNavigationClickListener() {
                        @Override
                        public void onNavigationClick() {
                            switch (viewModel.getDestinations().size()) {
                                case 0:
                                    Log.e(TAG, "onNavigationIconClick with no destinations. Code flow prevents this. git blame!");
                                    return;
                                case 1:
                                    // navigate directly to the single destination;
                                    Intent navigationIntent = NavigationIntentService.getNavigationIntent(viewModel.getDestinations().get(0));
                                    if (navigationIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                        startActivity(navigationIntent);
                                    } else {
                                        Toast.makeText(getContext(), R.string.error_message_system_missing_google_maps, Toast.LENGTH_LONG).show();
                                    }
                                    return;
                                default:
                                    // show the picker
                                    NavigationPickerFragment pickerFragment = NavigationPickerFragment.newInstance(tripId, dayNodeKey);
                                    pickerFragment.show(getChildFragmentManager(), ArgumentKeys.TAG_PICK_NAVIGATION_DIALOG);
                            }
                        }
                    });
                }
            }

            private boolean isToday(LocalDate tripDayDate) {
                return LocalDate.now().equals(tripDayDate);
            }
        };

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_trip_detail, container, false);
        ButterKnife.bind(this, rootView);

        DatabaseReference tripReference = tripRepository.getTrip(userId, tripNodeKey);
        tripReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                if (trip == null) {
                    Log.e(TAG, "onCreate - onDataChange: Trip object is null from Firebase");
                    tripDescriptionForDisplay = getString(R.string.phrase_for_YourTrip);
                    return;
                }
                tripDescriptionForDisplay = trip.getDescription();
                tripDescription.setText(tripDescriptionForDisplay);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error loading tripId: " + tripId + ", tripNodeKey: " + tripNodeKey);
                fragmentNavigationRequestListener.onFragmentNavigationRequest(FragmentTags.TAG_TRIP_LIST);
            }
        });

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
        outState.putString(ArgumentKeys.KEY_TRIP_ID, tripId);
        outState.putString(ArgumentKeys.KEY_TRIP_NODE_KEY, tripNodeKey);
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
        if (context instanceof TripDisplayCommunicationListener) {
            tripDisplayCommunicationListener = (TripDisplayCommunicationListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement TripDisplayCommunicationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentNavigationRequestListener = null;
    }
}
