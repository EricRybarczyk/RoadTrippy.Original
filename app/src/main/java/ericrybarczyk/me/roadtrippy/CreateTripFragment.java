package ericrybarczyk.me.roadtrippy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.util.DateUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateTripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTripFragment extends Fragment implements DatePickerFragment.TripDateSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG_DEPARTURE_DATE_DIALOG = "departure_date_dialog";
    private static final String TAG_RETURN_DATE_DIALOG= "return_date_dialog";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GregorianCalendar departureDate, returnDate;

    @BindView(R.id.trip_name_text) protected EditText tripNameText;
    @BindView(R.id.departure_date_button) protected Button departureDateButton;
    @BindView(R.id.return_date_button) protected Button returnDateButton;
    @BindView(R.id.origin_button) protected Button originButton;
    @BindView(R.id.destination_button) protected Button destinationButton;
    @BindView(R.id.option_return_directions) protected CheckBox optionReturnDirections;

    private OnFragmentInteractionListener fragmentInteractionListener;
    private static final String TAG = CreateTripFragment.class.getSimpleName();
    private static final String KEY_DEPARTURE_DATE = "departure_date_object";
    private static final String KEY_RETURN_DATE = "return_date_object";

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
            DatePickerFragment datePickerDialog = new DatePickerFragment();

            Bundle args = new Bundle();
            args.putSerializable(DatePickerFragment.KEY_CALENDAR_FOR_DISPLAY, departureDate);
            datePickerDialog.setArguments(args);

            datePickerDialog.setTripDateSelectedListener(this);
            datePickerDialog.show(getChildFragmentManager(), TAG_DEPARTURE_DATE_DIALOG);
        });
        returnDateButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick for returnDateButton");
            DatePickerFragment datePickerDialog = new DatePickerFragment();

            Bundle args = new Bundle();
            args.putSerializable(DatePickerFragment.KEY_CALENDAR_FOR_DISPLAY, returnDate);
            datePickerDialog.setArguments(args);

            datePickerDialog.setTripDateSelectedListener(this);
            datePickerDialog.show(getChildFragmentManager(), TAG_RETURN_DATE_DIALOG);
        });

        // originButton open a custom dialog where they can pick "Home" as starting point or else "somewhere else" which would take them to a map/search

        // destinationButton might just show the map/search but maybe give option to pick from a "trip idea"


        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        if (departureDate != null) {
            savedInstanceState.putSerializable(KEY_DEPARTURE_DATE, departureDate);
        }
        if (returnDate != null) {
            savedInstanceState.putSerializable(KEY_RETURN_DATE, returnDate);
        }
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
        // TODO: consider changing param to a GregorianCalendar (or Calendar?) to avoid instantiating new objects all the time
        if (tag.equals(TAG_DEPARTURE_DATE_DIALOG)) {
            GregorianCalendar baseDate = new GregorianCalendar(year, month, dayOfMonth);
            departureDate = baseDate;
            departureDateButton.setText(DateUtils.formatDate(departureDate));
            // also bump the returnDate so display will base calendar on the following day
            baseDate.add(Calendar.DATE, 1);
            returnDate = baseDate;
            Log.i(TAG, "onDateSelected: DEPART: " + String.valueOf(departureDate.get(Calendar.MONTH)+1) + "/" + departureDate.get(Calendar.DATE));
        } else if (tag.equals(TAG_RETURN_DATE_DIALOG)) {
            returnDate =  new GregorianCalendar(year, month, dayOfMonth);
            returnDateButton.setText(DateUtils.formatDate(returnDate));
            Log.i(TAG, "onDateSelected: RETURN: " + String.valueOf(returnDate.get(Calendar.MONTH)+1) + "/" + returnDate.get(Calendar.DATE));
        }
    }


}
