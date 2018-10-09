package ericrybarczyk.me.roadtrippy;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ericrybarczyk.me.roadtrippy.dto.TripDay;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;
import ericrybarczyk.me.roadtrippy.util.FontManager;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.util.InputUtils;
import ericrybarczyk.me.roadtrippy.util.RequestCodes;
import ericrybarczyk.me.roadtrippy.viewmodels.TripDayViewModel;

public class TripDayFragment extends Fragment {

    public static final String KEY_TRIP_ID = "trip_id_key";
    public static final String KEY_DAY_NODE_KEY = "trip_day_node_key";
    public static final String KEY_TRIP_DAY_NUMBER = "trip_day_number_key";

    @BindView(R.id.day_number_header) protected TextView dayNumberHeader;
    @BindView(R.id.icon_highlight) protected TextView iconHighlight;
    @BindView(R.id.day_primary_description) protected EditText dayPrimaryDescription;
    @BindView(R.id.destination_item) protected EditText destinationItem;
    @BindView(R.id.search_destination_button) protected ImageButton searchDestinationButton;
    @BindView(R.id.destination_list_label) protected TextView destinationListLabel;
    @BindView(R.id.day_destination_list) protected RecyclerView dayDestinationRecyclerView;
    @BindView(R.id.day_user_notes) protected EditText dayUserNotes;
    @BindView(R.id.save_trip_day_button) protected Button saveTripDayButton;

    private String tripId;
    private String tripNodeKey;
    private String dayNodeKey;
    private int dayNumber;
    String userId;
    TripRepository tripRepository;
    TripDayViewModel tripDayViewModel;
    private MapDisplayRequestListener mapDisplayRequestListener;
    private FragmentNavigationRequestListener fragmentNavigationRequestListener;
    private static final String TAG = TripDayFragment.class.getSimpleName();

    public TripDayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean stateInitialized = false;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_TRIP_ID)) {
                tripId = savedInstanceState.getString(KEY_TRIP_ID);
                tripNodeKey = savedInstanceState.getString(TripDetailFragment.KEY_TRIP_NODE_KEY);
                dayNodeKey = savedInstanceState.getString(KEY_DAY_NODE_KEY);
                dayNumber = savedInstanceState.getInt(KEY_TRIP_DAY_NUMBER);
                stateInitialized = true;
            }
        } else if (getArguments() != null) {
            tripId = getArguments().getString(KEY_TRIP_ID);
            tripNodeKey = getArguments().getString(TripDetailFragment.KEY_TRIP_NODE_KEY);
            dayNodeKey = getArguments().getString(KEY_DAY_NODE_KEY);
            dayNumber = getArguments().getInt(KEY_TRIP_DAY_NUMBER);
            stateInitialized = true;
        }
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        tripDayViewModel = ViewModelProviders.of(getActivity()).get(TripDayViewModel.class);

        if (!stateInitialized) {
            // this happens when we get here after adding a destination to this TripDay
            tripId = tripDayViewModel.getTripId();
            tripNodeKey = tripDayViewModel.getTripNodeKey();
            dayNodeKey = tripDayViewModel.getTripDayNodeKey();
            dayNumber = tripDayViewModel.getDayNumber();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_trip_day, container, false);
        ButterKnife.bind(this, rootView);
        InputUtils.hideKeyboardFrom(getContext(), rootView);

        tripRepository = new TripRepository();
        DatabaseReference reference = tripRepository.getTripDay(userId, tripId, dayNodeKey);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TripDay tripDay = dataSnapshot.getValue(TripDay.class);
                if (tripDay == null) {
                    Log.e(TAG, "onCreateView - onDataChange: TripDay object is null from Firebase");
                    return;
                }
                tripDayViewModel.updateFrom(tripDay); // tripDayViewModel = TripDayViewModel.from(tripDay);

                setHighlightIndicator(tripDayViewModel.getIsHighlight());
                if (!tripDayViewModel.getIsDefaultText()) {
                    dayPrimaryDescription.setText(tripDayViewModel.getPrimaryDescription());
                    dayUserNotes.setText(tripDayViewModel.getUserNotes());
                }

                if (tripDayViewModel.getDestinations().size() > 0) {
                    dayDestinationRecyclerView.setVisibility(View.VISIBLE);
                    destinationListLabel.setVisibility(View.VISIBLE);
                    TripLocationAdapter adapter = new TripLocationAdapter(
                            tripDayViewModel.getDestinations(),
                            userId,
                            tripId,
                            dayNodeKey
                    );
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    dayDestinationRecyclerView.setLayoutManager(layoutManager);
                    dayDestinationRecyclerView.setAdapter(adapter);
                } else {
                    dayDestinationRecyclerView.setVisibility(View.INVISIBLE);
                    destinationListLabel.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error loading tripId: " + tripId);
                fragmentNavigationRequestListener.onFragmentNavigationRequest(FragmentTags.TAG_TRIP_LIST);
            }
        });

        String headerText = getString(R.string.word_for_Day) + " " + String.valueOf(dayNumber);
        dayNumberHeader.setText(headerText);

        // provide map fragment for location searching
        searchDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTripDay();
                mapDisplayRequestListener.onMapDisplayRequested(RequestCodes.TRIP_DAY_DESTINATION_REQUEST_CODE, FragmentTags.TAG_TRIP_DAY);
            }
        });


        rootView.clearFocus();
        return rootView;
    }

    private void setHighlightIndicator(boolean isHighlight) {
        if (isHighlight) {
            iconHighlight.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME_SOLID));
            iconHighlight.setTextColor(ContextCompat.getColor(getContext(), R.color.colorControlHighlight));
        } else {
            iconHighlight.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME_REGULAR));
            iconHighlight.setTextColor(ContextCompat.getColor(getContext(), R.color.colorControlHighlightOff));
        }
    }

    @OnClick(R.id.icon_highlight)
    public void onHighlightClick() {
        tripDayViewModel.setIsHighlight(!tripDayViewModel.getIsHighlight());
        tripRepository.updateTripDayHighlight(userId, tripId, dayNodeKey, tripDayViewModel.getIsHighlight());
        setHighlightIndicator(tripDayViewModel.getIsHighlight());
    }

    @OnClick(R.id.save_trip_day_button)
    public void onSaveClick() {
        saveTripDay();
        fragmentNavigationRequestListener.onFragmentNavigationRequest(FragmentTags.TAG_TRIP_DETAIL, tripId, tripNodeKey);
    }

    private void saveTripDay() {
        tripDayViewModel.setPrimaryDescription(dayPrimaryDescription.getText().toString().trim());
        tripDayViewModel.setUserNotes(dayUserNotes.getText().toString().trim());
        tripDayViewModel.setIsDefaultText(false);
        tripDayViewModel.setTripDayNodeKey(dayNodeKey);
        tripRepository.updateTripDay(userId, tripId, dayNodeKey, tripDayViewModel.asTripDay());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_TRIP_ID, tripId);
        outState.putString(TripDetailFragment.KEY_TRIP_NODE_KEY, tripNodeKey);
        outState.putString(KEY_DAY_NODE_KEY, dayNodeKey);
        outState.putInt(KEY_TRIP_DAY_NUMBER, dayNumber);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapDisplayRequestListener) {
            mapDisplayRequestListener = (MapDisplayRequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MapDisplayRequestListener");
        }
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
