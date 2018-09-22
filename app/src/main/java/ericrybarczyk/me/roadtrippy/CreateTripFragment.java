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
                    DatePickerFragment.TripDateSelectedListener,
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
        InputUtils.hideKeyboardFrom(getContext(), rootView);

        // TODO: probably need to refine isEdited to be per-value and not just the ViewModel as a whole, so only edited items update the display
        if (tripViewModel.isEdited()) {
            departureDateButton.setText(DateUtils.formatDate(tripViewModel.getDepartureDate()));
            returnDateButton.setText(DateUtils.formatDate(tripViewModel.getReturnDate()));
            originButton.setText(tripViewModel.getOriginDescription());
            destinationButton.setText(tripViewModel.getDestinationDescription());
        }

        departureDateButton.setOnClickListener(v -> {
            departureDateButton.requestFocus();
            InputUtils.hideKeyboardFrom(getContext(), rootView);
            DatePickerFragment datePickerDialog = new DatePickerFragment();

            Bundle args = new Bundle();
            args.putSerializable(DatePickerFragment.KEY_CALENDAR_FOR_DISPLAY, tripViewModel.getDepartureDate());
            datePickerDialog.setArguments(args);

            datePickerDialog.setTripDateSelectedListener(this);
            datePickerDialog.show(getChildFragmentManager(), FragmentTags.TAG_DEPARTURE_DATE_DIALOG);
        });
        returnDateButton.setOnClickListener(v -> {
            returnDateButton.requestFocus();
            InputUtils.hideKeyboardFrom(getContext(), rootView);
            DatePickerFragment datePickerDialog = new DatePickerFragment();

            Bundle args = new Bundle();
            args.putSerializable(DatePickerFragment.KEY_CALENDAR_FOR_DISPLAY, tripViewModel.getReturnDate());
            datePickerDialog.setArguments(args);

            datePickerDialog.setTripDateSelectedListener(this);
            datePickerDialog.show(getChildFragmentManager(), FragmentTags.TAG_RETURN_DATE_DIALOG);
        });

        originButton.setOnClickListener(v -> {
            TripOriginPickerFragment pickerFragment = TripOriginPickerFragment.newInstance(this);
            pickerFragment.show(getChildFragmentManager(), TAG_PICK_ORIGIN_DIALOG);
        });

        destinationButton.setOnClickListener(v -> {
            mapDisplayRequestListener.onMapDisplayRequested(this, RequestCodes.TRIP_DESTINATION_REQUEST_CODE, FragmentTags.FRAG_TAG_CREATE_TRIP);
        });


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
    public void onLocationSelected(LatLng location, int requestCode, String locationDescription) {
        switch (requestCode) {
            case RequestCodes.TRIP_ORIGIN_REQUEST_CODE:
                tripViewModel.setOriginLatLng(location);
                tripViewModel.setOriginDescription(locationDescription);
                break;
            case RequestCodes.TRIP_DESTINATION_REQUEST_CODE:
                tripViewModel.setDestinationLatLng(location);
                tripViewModel.setDestinationDescription(locationDescription);
                break;
            default:
                throw new IllegalArgumentException("Invalid requestCode argument: " + String.valueOf(requestCode));
        }
    }

    @Override
    public void onTripDateSelected(int year, int month, int dayOfMonth, String tag) {
        if (tag.equals(FragmentTags.TAG_DEPARTURE_DATE_DIALOG)) {
            tripViewModel.setDepartureDate(new GregorianCalendar(year, month, dayOfMonth));
            if (tripViewModel.isEdited()) {
                departureDateButton.setText(DateUtils.formatDate(tripViewModel.getDepartureDate()));
            }
            Log.i(TAG, "onDateSelected: DEPART: " + String.valueOf(tripViewModel.getDepartureDate().get(Calendar.MONTH)+1) + "/" + tripViewModel.getDepartureDate().get(Calendar.DATE));

        } else if (tag.equals(FragmentTags.TAG_RETURN_DATE_DIALOG)) {
            tripViewModel.setReturnDate(new GregorianCalendar(year, month, dayOfMonth));
            if (tripViewModel.isEdited()) {
                returnDateButton.setText(DateUtils.formatDate(tripViewModel.getReturnDate()));
            }
            Log.i(TAG, "onDateSelected: RETURN: " + String.valueOf(tripViewModel.getReturnDate().get(Calendar.MONTH)+1) + "/" + tripViewModel.getReturnDate().get(Calendar.DATE));
        }
    }

    @Override
    public void onTripOriginSelected(String key) {
        Log.i(TAG, "onTripOriginSelected: key = " + key);
        if (key.equals(TripOriginPickerFragment.KEY_HOME_ORIGIN)) {
            tripViewModel.setOriginLatLng(HOME_LOCATION);
        } else {
            mapDisplayRequestListener.onMapDisplayRequested(this, RequestCodes.TRIP_ORIGIN_REQUEST_CODE, FragmentTags.FRAG_TAG_CREATE_TRIP);
        }
    }

    public interface MapDisplayRequestListener {
        void onMapDisplayRequested(GoogleMapFragment.LocationSelectedListener callback, int requestCode, String returnToFragmentTag);
    }

}
