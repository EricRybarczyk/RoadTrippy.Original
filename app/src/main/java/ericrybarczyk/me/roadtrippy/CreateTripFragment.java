package ericrybarczyk.me.roadtrippy;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateTripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTripFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.trip_name_text) protected EditText tripNameText;
//    @BindView(R.id.departure_date_value) protected EditText departureDateText;
//    @BindView(R.id.return_date_value) protected EditText returnDateText;
    @BindView(R.id.departure_date_button) protected Button departureDateButton;
    @BindView(R.id.return_date_button) protected Button returnDateButton;
    @BindView(R.id.origin_value) protected EditText originText;
    @BindView(R.id.destination_value) protected EditText destinationText;
    @BindView(R.id.option_return_directions) protected CheckBox optionReturnDirections;

    private OnFragmentInteractionListener fragmentInteractionListener;
    private static final String TAG = CreateTripFragment.class.getSimpleName();

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

        departureDateButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick for departureDateButton");
            DialogFragment datePickerDialog = new DatePickerFragment();
            datePickerDialog.show(getChildFragmentManager(), "departureDatePicker");
        });
        returnDateButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick for returnDateButton");
            DialogFragment datePickerDialog = new DatePickerFragment();
            datePickerDialog.show(getChildFragmentManager(), "returnDatePicker");
        });

//        departureDateText.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                Log.i(TAG, "onFocusChange for departureDateText");
//                InputUtil.hideKeyboardFrom(v.getContext(), v.getRootView());
//                DialogFragment datePickerDialog = new DatePickerFragment();
//                datePickerDialog.show(getChildFragmentManager(), "departureDatePicker");
//            }
//        });
//
//        returnDateText.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                Log.i(TAG, "onFocusChange for returnDateText");
//                DialogFragment datePickerDialog = new DatePickerFragment();
//                datePickerDialog.show(getChildFragmentManager(), "returnDatePicker");
//            }
//        });

        return rootView;
    }

//    @OnClick(R.id.departure_date_button) onClick()



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
}
