package ericrybarczyk.me.roadtrippy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import ericrybarczyk.me.roadtrippy.util.InputUtils;
import ericrybarczyk.me.roadtrippy.util.RequestCodes;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateTripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTripFragment extends Fragment
        implements  DatePickerFragment.TripDateSelectedListener,
                    TripOriginPickerFragment.TripOriginSelectedListener,
                    GoogleMapFragment.LocationSelectedListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG_DEPARTURE_DATE_DIALOG = "departure_date_dialog";
    private static final String TAG_RETURN_DATE_DIALOG= "return_date_dialog";
    private static final String TAG_PICK_ORIGIN_DIALOG= "pick_origin_dialog";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String tripDescription;
    private GregorianCalendar departureDate, returnDate;
    private LatLng originLatLng, destinationLatLng;
    private boolean includeReturnDirections = true;

    @BindView(R.id.trip_name_text) protected EditText tripNameText;
    @BindView(R.id.departure_date_button) protected Button departureDateButton;
    @BindView(R.id.return_date_button) protected Button returnDateButton;
    @BindView(R.id.origin_button) protected Button originButton;
    @BindView(R.id.destination_button) protected Button destinationButton;
    @BindView(R.id.option_return_directions) protected CheckBox optionReturnDirections;

    private OnFragmentInteractionListener fragmentInteractionListener;
    private MapDisplayRequestListener mapDisplayRequestListener;
    private static final String TAG = CreateTripFragment.class.getSimpleName();

    private static final String KEY_TRIP_DESCRIPTION = "trip_description";
    private static final String KEY_DEPARTURE_DATE = "departure_date_object";
    private static final String KEY_RETURN_DATE = "return_date_object";
    private static final String KEY_ORIGIN_LATLNG = "origin_lat_lng";
    private static final String KEY_DESTINATION_LATLNG = "destination_lat_lng";
    private static final String KEY_INCLUDE_RETURN_DIRECTIONS = "include_return_directions";

    // TODO: implement preference for user's home location
    private static final LatLng HOME_LOCATION = new LatLng(36.375148,-94.207480);

    public CreateTripFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateTripFragment newInstance(String param1, String param2) {
        CreateTripFragment fragment = new CreateTripFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_TRIP_DESCRIPTION)) {
                tripDescription = savedInstanceState.getString(KEY_TRIP_DESCRIPTION);
            }
            if (savedInstanceState.containsKey(KEY_DEPARTURE_DATE)) {
                departureDate = (GregorianCalendar) savedInstanceState.getSerializable(KEY_DEPARTURE_DATE);
            }
            if (savedInstanceState.containsKey(KEY_RETURN_DATE)) {
                returnDate = (GregorianCalendar) savedInstanceState.getSerializable(KEY_RETURN_DATE);
            }
            if (savedInstanceState.containsKey(KEY_ORIGIN_LATLNG)) {
                originLatLng = savedInstanceState.getParcelable(KEY_ORIGIN_LATLNG);
            }
            if (savedInstanceState.containsKey(KEY_DESTINATION_LATLNG)) {
                destinationLatLng = savedInstanceState.getParcelable(KEY_DESTINATION_LATLNG);
            }
            if (savedInstanceState.containsKey(KEY_INCLUDE_RETURN_DIRECTIONS)) {
                includeReturnDirections = savedInstanceState.getBoolean(KEY_INCLUDE_RETURN_DIRECTIONS);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_create_trip, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            if (savedInstanceState.getSerializable(KEY_DEPARTURE_DATE) != null) {
                departureDate = (GregorianCalendar) savedInstanceState.getSerializable(KEY_DEPARTURE_DATE);
                departureDateButton.setText(DateUtils.formatDate(departureDate));
            }
            if (savedInstanceState.getSerializable(KEY_RETURN_DATE) != null) {
                returnDate = (GregorianCalendar) savedInstanceState.getSerializable(KEY_RETURN_DATE);
                returnDateButton.setText(DateUtils.formatDate(returnDate));
            }
        }
        if (departureDate == null) { departureDate = new GregorianCalendar(); }
        if (returnDate == null) { returnDate = new GregorianCalendar(); }

        departureDateButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick for departureDateButton");
            InputUtils.hideKeyboardFrom(getContext(), rootView);
            DatePickerFragment datePickerDialog = new DatePickerFragment();

            Bundle args = new Bundle();
            args.putSerializable(DatePickerFragment.KEY_CALENDAR_FOR_DISPLAY, departureDate);
            datePickerDialog.setArguments(args);

            datePickerDialog.setTripDateSelectedListener(this);
            datePickerDialog.show(getChildFragmentManager(), TAG_DEPARTURE_DATE_DIALOG);
        });
        returnDateButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick for returnDateButton");
            InputUtils.hideKeyboardFrom(getContext(), rootView);
            DatePickerFragment datePickerDialog = new DatePickerFragment();

            Bundle args = new Bundle();
            args.putSerializable(DatePickerFragment.KEY_CALENDAR_FOR_DISPLAY, returnDate);
            datePickerDialog.setArguments(args);

            datePickerDialog.setTripDateSelectedListener(this);
            datePickerDialog.show(getChildFragmentManager(), TAG_RETURN_DATE_DIALOG);
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
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        if (tripDescription != null) {
            savedInstanceState.putString(KEY_TRIP_DESCRIPTION, tripDescription);
        }
        if (departureDate != null) {
            savedInstanceState.putSerializable(KEY_DEPARTURE_DATE, departureDate);
        }
        if (returnDate != null) {
            savedInstanceState.putSerializable(KEY_RETURN_DATE, returnDate);
        }
        if (originLatLng != null) {
            savedInstanceState.putParcelable(KEY_ORIGIN_LATLNG, originLatLng);
        }
        if (destinationLatLng != null) {
            savedInstanceState.putParcelable(KEY_DESTINATION_LATLNG, destinationLatLng);
        }
        savedInstanceState.putBoolean(KEY_INCLUDE_RETURN_DIRECTIONS, includeReturnDirections);

        super.onSaveInstanceState(savedInstanceState);
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            fragmentInteractionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onLocationSelected(LatLng location, int requestCode) {
        switch (requestCode) {
            case RequestCodes.TRIP_ORIGIN_REQUEST_CODE:
                originLatLng = location;
                originButton.setText(String.valueOf(requestCode)); // TODO: shift to a Model object that has appropriate text
                break;
            case RequestCodes.TRIP_DESTINATION_REQUEST_CODE:
                destinationLatLng = location;
                destinationButton.setText(String.valueOf(requestCode)); // TODO: shift to a Model object that has appropriate text
                break;
            default:
                throw new IllegalArgumentException("Invalid requestCode argument: " + String.valueOf(requestCode));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onTripDateSelected(int year, int month, int dayOfMonth, String tag) {
        if (tag.equals(TAG_DEPARTURE_DATE_DIALOG)) {
            departureDate = new GregorianCalendar(year, month, dayOfMonth);
            departureDateButton.setText(DateUtils.formatDate(departureDate));
            Log.i(TAG, "onDateSelected: DEPART: " + String.valueOf(departureDate.get(Calendar.MONTH)+1) + "/" + departureDate.get(Calendar.DATE));

            // prevent illogical returnDate before departureDate
            if (departureDate.compareTo(returnDate) > 0) {
                // bump the returnDate so display will base calendar on the following day
                GregorianCalendar baseDate = new GregorianCalendar(year, month, dayOfMonth);
                baseDate.add(Calendar.DATE, 1);
                returnDate = baseDate;
            }

        } else if (tag.equals(TAG_RETURN_DATE_DIALOG)) {
            returnDate =  new GregorianCalendar(year, month, dayOfMonth);
            returnDateButton.setText(DateUtils.formatDate(returnDate));
            Log.i(TAG, "onDateSelected: RETURN: " + String.valueOf(returnDate.get(Calendar.MONTH)+1) + "/" + returnDate.get(Calendar.DATE));
        }
    }

    @Override
    public void onTripOriginSelected(String key) {
        Log.i(TAG, "onTripOriginSelected: key = " + key);
        if (key.equals(TripOriginPickerFragment.KEY_HOME_ORIGIN)) {
            originLatLng = HOME_LOCATION;
        } else {
            // show them a map to find their starting location;
            if (this.mapDisplayRequestListener == null) {
                this.setMapDisplayRequestListener((MapDisplayRequestListener) getActivity());
            }
            this.mapDisplayRequestListener.onMapDisplayRequested(this, RequestCodes.TRIP_ORIGIN_REQUEST_CODE);
        }

    }

    public void setMapDisplayRequestListener(MapDisplayRequestListener listener) {
        this.mapDisplayRequestListener = listener;
    }

    public interface MapDisplayRequestListener {
        void onMapDisplayRequested(GoogleMapFragment.LocationSelectedListener callback, int requestCode);
    }

}
