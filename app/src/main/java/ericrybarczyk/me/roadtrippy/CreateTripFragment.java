package ericrybarczyk.me.roadtrippy;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.util.DateUtils;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.util.InputUtils;
import ericrybarczyk.me.roadtrippy.util.RequestCodes;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;


public class CreateTripFragment extends Fragment
        implements  TripOriginPickerFragment.TripOriginSelectedListener,
                    GoogleMapFragment.LocationSelectedListener {

    private TripViewModel tripViewModel;

    @BindView(R.id.trip_name_text) protected EditText tripNameText;
    @BindView(R.id.departure_date_button) protected Button departureDateButton;
    @BindView(R.id.return_date_button) protected Button returnDateButton;
    @BindView(R.id.origin_button) protected Button originButton;
    @BindView(R.id.destination_button) protected Button destinationButton;
    @BindView(R.id.option_return_directions) protected CheckBox optionReturnDirections;

    private MapDisplayRequestListener mapDisplayRequestListener;
    private static final String TAG = CreateTripFragment.class.getSimpleName();
    private static final String TAG_PICK_ORIGIN_DIALOG= "pick_origin_dialog";

    // TODO: implement preference for user's home location
    private static final LatLng HOME_LOCATION = new LatLng(36.375148,-94.207480);

    public CreateTripFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripViewModel = ViewModelProviders.of(getActivity()).get(TripViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_create_trip, container, false);
        ButterKnife.bind(this, rootView);

        // TODO: handle not showing the date if user hasn't actually set anything yet (don't show default date unless they picked that date)
        departureDateButton.setText(DateUtils.formatDate(tripViewModel.getStartDate()));
        returnDateButton.setText(DateUtils.formatDate(tripViewModel.getEndDate()));

        departureDateButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick for departureDateButton");
            InputUtils.hideKeyboardFrom(getContext(), rootView);
            DatePickerFragment datePickerDialog = new DatePickerFragment();

            Bundle args = new Bundle();
            args.putSerializable(DatePickerFragment.KEY_CALENDAR_FOR_DISPLAY, tripViewModel.getStartDate());
            datePickerDialog.setArguments(args);

//            datePickerDialog.setTripDateSelectedListener(this);
            datePickerDialog.show(getChildFragmentManager(), FragmentTags.TAG_DEPARTURE_DATE_DIALOG);
        });
        returnDateButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick for returnDateButton");
            InputUtils.hideKeyboardFrom(getContext(), rootView);
            DatePickerFragment datePickerDialog = new DatePickerFragment();

            Bundle args = new Bundle();
            args.putSerializable(DatePickerFragment.KEY_CALENDAR_FOR_DISPLAY, tripViewModel.getEndDate());
            datePickerDialog.setArguments(args);

//            datePickerDialog.setTripDateSelectedListener(this);
            datePickerDialog.show(getChildFragmentManager(), FragmentTags.TAG_RETURN_DATE_DIALOG);
        });

        // TODO: originButton open a custom dialog where they can pick "Home" as starting point or else "somewhere else" which would take them to a map/search
        originButton.setOnClickListener(v -> {
            TripOriginPickerFragment pickerFragment = TripOriginPickerFragment.newInstance(this);
            pickerFragment.show(getChildFragmentManager(), TAG_PICK_ORIGIN_DIALOG);
        });

        // TODO: destinationButton might just show the map/search but maybe give option to pick from a "trip idea"

        rootView.clearFocus(); // TODO: test if this helps prevent showing keyboard when app is opened from background
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapDisplayRequestListener) {
            mapDisplayRequestListener = (MapDisplayRequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFabDisplayRequestListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mapDisplayRequestListener = null;
    }

    @Override
    public void onLocationSelected(LatLng location, int requestCode) {
        switch (requestCode) {
            case RequestCodes.TRIP_ORIGIN_REQUEST_CODE:
                tripViewModel.setOriginLatLng(location);
                originButton.setText(String.valueOf(requestCode)); // TODO: shift to a Model object that has appropriate text
                break;
            case RequestCodes.TRIP_DESTINATION_REQUEST_CODE:
                tripViewModel.setDestinationLatLng(location);
                destinationButton.setText(String.valueOf(requestCode)); // TODO: shift to a Model object that has appropriate text
                break;
            default:
                throw new IllegalArgumentException("Invalid requestCode argument: " + String.valueOf(requestCode));
        }
    }


//    @Override
    // TODO: implement the logic somewhere, just not via the callback since shared ViewModel took over
    public void onTripDateSelected(int year, int month, int dayOfMonth, String tag) {
        if (tag.equals(FragmentTags.TAG_DEPARTURE_DATE_DIALOG)) {
            tripViewModel.setStartDate(new GregorianCalendar(year, month, dayOfMonth));
            departureDateButton.setText(DateUtils.formatDate(tripViewModel.getStartDate()));
            Log.i(TAG, "onDateSelected: DEPART: " + String.valueOf(tripViewModel.getStartDate().get(Calendar.MONTH)+1) + "/" + tripViewModel.getStartDate().get(Calendar.DATE));

            // prevent illogical returnDate before departureDate
            if (tripViewModel.getStartDate().compareTo(tripViewModel.getEndDate()) > 0) {
                // bump the returnDate so display will base calendar on the following day
                GregorianCalendar baseDate = new GregorianCalendar(year, month, dayOfMonth);
                baseDate.add(Calendar.DATE, 1);
                tripViewModel.setEndDate(baseDate);
            }

        } else if (tag.equals(FragmentTags.TAG_RETURN_DATE_DIALOG)) {
            tripViewModel.setEndDate(new GregorianCalendar(year, month, dayOfMonth));
            returnDateButton.setText(DateUtils.formatDate(tripViewModel.getEndDate()));
            Log.i(TAG, "onDateSelected: RETURN: " + String.valueOf(tripViewModel.getEndDate().get(Calendar.MONTH)+1) + "/" + tripViewModel.getEndDate().get(Calendar.DATE));
        }
    }

    @Override
    public void onTripOriginSelected(String key) {
        Log.i(TAG, "onTripOriginSelected: key = " + key);
        if (key.equals(TripOriginPickerFragment.KEY_HOME_ORIGIN)) {
            tripViewModel.setOriginLatLng(HOME_LOCATION);
        } else {
            mapDisplayRequestListener.onMapDisplayRequested(this, RequestCodes.TRIP_ORIGIN_REQUEST_CODE);
        }
    }

    public interface MapDisplayRequestListener {
        void onMapDisplayRequested(GoogleMapFragment.LocationSelectedListener callback, int requestCode);
    }

}
