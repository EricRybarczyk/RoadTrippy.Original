package ericrybarczyk.me.roadtrippy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.util.FontManager;

public class TripDayFragment extends Fragment {

    public static final String KEY_TRIP_ID = "trip_id_key";
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
    private int dayNumber;

    public TripDayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_TRIP_ID)) {
                tripId = savedInstanceState.getString(KEY_TRIP_ID);
                dayNumber = savedInstanceState.getInt(KEY_TRIP_DAY_NUMBER);
            }
        } else if (getArguments() != null) {
            tripId = getArguments().getString(KEY_TRIP_ID);
            dayNumber = getArguments().getInt(KEY_TRIP_DAY_NUMBER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_trip_day, container, false);
        ButterKnife.bind(this, rootView);

        String headerText = getString(R.string.word_for_Day) + " " + String.valueOf(dayNumber);
        dayNumberHeader.setText(headerText);

        iconHighlight.setTypeface(FontManager.getTypeface(getContext(), FontManager.FONTAWESOME_SOLID));

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_TRIP_ID, tripId);
        outState.putInt(KEY_TRIP_DAY_NUMBER, dayNumber);
        super.onSaveInstanceState(outState);
    }
}
