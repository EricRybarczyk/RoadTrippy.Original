package ericrybarczyk.me.roadtrippy;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.util.InputUtils;
import ericrybarczyk.me.roadtrippy.util.RequestCodes;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;


public class CreateTripFragment extends Fragment
        implements  TripOriginPickerFragment.TripOriginSelectedListener,
                    DatePickerFragment.TripDateSelectedListener {

    private TripViewModel tripViewModel;

    @BindView(R.id.trip_name_text) protected EditText tripNameText;
    @BindView(R.id.departure_date_button) protected Button departureDateButton;
    @BindView(R.id.return_date_button) protected Button returnDateButton;
    @BindView(R.id.origin_button) protected Button originButton;
    @BindView(R.id.destination_button) protected Button destinationButton;
    @BindView(R.id.option_return_directions) protected CheckBox optionReturnDirections;
    @BindView(R.id.create_trip_next_button) protected Button nextStepButton;

    private MapDisplayRequestListener mapDisplayRequestListener;
    private FragmentNavigationRequestListener fragmentNavigationRequestListener;
    private static final String TAG = CreateTripFragment.class.getSimpleName();
    private static final String TAG_PICK_ORIGIN_DIALOG= "pick_origin_dialog";


    public CreateTripFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripViewModel = ViewModelProviders.of(getActivity()).get(TripViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_create_trip, container, false);
        ButterKnife.bind(this, rootView);
        InputUtils.hideKeyboardFrom(getContext(), rootView);

        // TODO: probably need to refine isEdited to be per-value and not just the ViewModel as a whole, so only edited items update the display
        if (tripViewModel.isEdited()) {
            tripNameText.setText(tripViewModel.getDescription());
            departureDateButton.setText(tripViewModel.getDepartureDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
            returnDateButton.setText(tripViewModel.getReturnDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
            originButton.setText(tripViewModel.getOriginDescription());
            destinationButton.setText(tripViewModel.getDestinationDescription());
            optionReturnDirections.setChecked(tripViewModel.isIncludeReturn());
        } else {
            tripNameText.getText().clear();
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
            mapDisplayRequestListener.onMapDisplayRequested(RequestCodes.TRIP_DESTINATION_REQUEST_CODE, FragmentTags.TAG_CREATE_TRIP);
        });

        nextStepButton.setOnClickListener(v -> {
            tripViewModel.setDescription(tripNameText.getText().toString());
            if (isValidForSave()) {
                fragmentNavigationRequestListener.onFragmentNavigationRequest(FragmentTags.TAG_TRIP_OVERVIEW_MAP);
            } else {
                Toast.makeText(getContext(), R.string.error_create_trip_data_validation, Toast.LENGTH_LONG).show();
            }
        });

        rootView.clearFocus();
        return rootView;
    }

    private boolean isValidForSave() {
        return (!tripViewModel.getDescription().isEmpty())
                && (!tripViewModel.getOriginDescription().isEmpty())
                && (!tripViewModel.getDestinationDescription().isEmpty())
                && (tripViewModel.getDepartureDate().compareTo(tripViewModel.getReturnDate()) <= 0)
                && (tripViewModel.getOriginLatLng() != null)
                && (tripViewModel.getDestinationLatLng() != null);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!tripViewModel.isEdited()) {
            tripNameText.getText().clear(); // prevent prior trip editing from showing up when view model has been reset
        }
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
        mapDisplayRequestListener = null;
        fragmentNavigationRequestListener = null;
    }

    @Override
    public void onTripDateSelected(int year, int month, int dayOfMonth, String tag) {
        if (tag.equals(FragmentTags.TAG_DEPARTURE_DATE_DIALOG)) {
            tripViewModel.setDepartureDate(LocalDate.of(year, month, dayOfMonth));
            if (tripViewModel.isEdited()) {
                departureDateButton.setText(tripViewModel.getDepartureDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
            }

        } else if (tag.equals(FragmentTags.TAG_RETURN_DATE_DIALOG)) {
            tripViewModel.setReturnDate(LocalDate.of(year, month, dayOfMonth));
            if (tripViewModel.isEdited()) {
                returnDateButton.setText(tripViewModel.getReturnDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
            }
        }
    }

    @Override
    public void onTripOriginSelected(String key) {
        Log.i(TAG, "onTripOriginSelected: key = " + key);
        if (key.equals(TripOriginPickerFragment.KEY_HOME_ORIGIN)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            if (preferences.contains(getString(R.string.pref_key_home_latitude)) && preferences.contains(getString(R.string.pref_key_home_longitude))) {
                tripViewModel.setOriginLatLng(
                        new LatLng(
                                (double) preferences.getFloat(getString(R.string.pref_key_home_latitude), 0.0f),
                                (double) preferences.getFloat(getString(R.string.pref_key_home_longitude), 0.0f)
                        )
                );
                String home = getString(R.string.word_for_HOME);
                tripViewModel.setOriginDescription(home);
                originButton.setText(home);
            } else {
                Toast.makeText(getContext(), R.string.error_home_location_preference_not_found, Toast.LENGTH_LONG).show();
            }

        } else {
            mapDisplayRequestListener.onMapDisplayRequested(RequestCodes.TRIP_ORIGIN_REQUEST_CODE, FragmentTags.TAG_CREATE_TRIP);
        }
    }
}
