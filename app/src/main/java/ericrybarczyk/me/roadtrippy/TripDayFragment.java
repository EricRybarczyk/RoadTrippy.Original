package ericrybarczyk.me.roadtrippy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.dto.TripDay;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;
import ericrybarczyk.me.roadtrippy.util.FontManager;
import ericrybarczyk.me.roadtrippy.viewmodels.TripDayViewModel;
import ericrybarczyk.me.roadtrippy.viewmodels.TripLocationViewModel;

public class TripDayFragment extends Fragment {

    public static final String KEY_TRIP_ID = "trip_id_key";
    public static final String KEY_NODE_KEY = "trip_day_node_key";
    public static final String KEY_TRIP_DAY_NUMBER = "trip_day_number_key";

    @BindView(R.id.day_number_header) protected TextView dayNumberHeader;
    @BindView(R.id.icon_highlight) protected TextView iconHighlight;
    @BindView(R.id.day_primary_description) protected EditText dayPrimaryDescription;
    @BindView(R.id.destination_item) protected EditText destinationItem;
    @BindView(R.id.search_destination_button) protected ImageButton searchDestinationButton;
    @BindView(R.id.save_destination_button) protected Button saveDestinationButton;
    @BindView(R.id.day_destination_list) protected TextView dayDestinationList;
    @BindView(R.id.day_secondary_description) protected EditText daySecondaryDescription;
    @BindView(R.id.save_trip_day_button) protected Button saveTripDayButton;

    private String tripId;
    private String nodeKey;
    private int dayNumber;
    TripRepository tripRepository;
    private static final String TAG = TripDayFragment.class.getSimpleName();

    public TripDayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_TRIP_ID)) {
                tripId = savedInstanceState.getString(KEY_TRIP_ID);
                nodeKey = savedInstanceState.getString(KEY_NODE_KEY);
                dayNumber = savedInstanceState.getInt(KEY_TRIP_DAY_NUMBER);
            }
        } else if (getArguments() != null) {
            tripId = getArguments().getString(KEY_TRIP_ID);
            nodeKey = getArguments().getString(KEY_NODE_KEY);
            dayNumber = getArguments().getInt(KEY_TRIP_DAY_NUMBER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_trip_day, container, false);
        ButterKnife.bind(this, rootView);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();

        tripRepository = new TripRepository();
        DatabaseReference reference = tripRepository.getTripDay(userId, tripId, nodeKey);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TripDay tripDay = dataSnapshot.getValue(TripDay.class);
                if (tripDay == null) {
                    Log.e(TAG, "onCreateView - onDataChange: TripDay object is null");
                    return;
                }
                TripDayViewModel viewModel = TripDayViewModel.from(tripDay);

                setHighlightIndicator(viewModel.getIsHighlight());
                dayPrimaryDescription.setText(viewModel.getPrimaryDescription());
                daySecondaryDescription.setText(viewModel.getSecondaryDescription());
                if (viewModel.getDestinations().size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (TripLocationViewModel destination : viewModel.getDestinations()) {
                        sb.append(destination.getDescription());
                        sb.append(System.lineSeparator());
                    }
                    dayDestinationList.setText(sb.toString());
                } else {
                    // TODO: display something if nothing in this list?
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String headerText = getString(R.string.word_for_Day) + " " + String.valueOf(dayNumber);
        dayNumberHeader.setText(headerText);


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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_TRIP_ID, tripId);
        outState.putString(KEY_NODE_KEY, nodeKey);
        outState.putInt(KEY_TRIP_DAY_NUMBER, dayNumber);
        super.onSaveInstanceState(outState);
    }
}
