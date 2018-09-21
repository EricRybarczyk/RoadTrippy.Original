package ericrybarczyk.me.roadtrippy;

/*  Non-plagiarism statement: this class is directly adapted from
 *  https://developer.android.com/guide/topics/ui/controls/pickers#DatePicker
 */

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;

public class DatePickerFragment extends DialogFragment
        implements  DatePickerDialog.OnDateSetListener {

    private TripViewModel tripViewModel;
//    private TripDateSelectedListener tripDateSelectedListener;
    private static final String TAG = DatePickerFragment.class.getSimpleName();
    private Calendar calendarForDisplay = null;
    static final String KEY_CALENDAR_FOR_DISPLAY = "calendar_for_display";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        tripViewModel = ViewModelProviders.of(getActivity()).get(TripViewModel.class);

        assert getTag() != null;
        switch (getTag()) {
            case FragmentTags.TAG_DEPARTURE_DATE_DIALOG:
                calendarForDisplay = tripViewModel.getStartDate();
                break;
            case FragmentTags.TAG_RETURN_DATE_DIALOG:
                calendarForDisplay = tripViewModel.getEndDate();
                break;
        }

//        if (savedInstanceState != null) {
//            if (savedInstanceState.getSerializable(KEY_CALENDAR_FOR_DISPLAY) != null) {
//                calendarForDisplay = (GregorianCalendar) savedInstanceState.getSerializable(KEY_CALENDAR_FOR_DISPLAY);
//            }
//        }
//        if (calendarForDisplay == null) {
//            Bundle args = this.getArguments();
//            if (args != null && args.getSerializable(KEY_CALENDAR_FOR_DISPLAY) != null) {
//                calendarForDisplay = (GregorianCalendar) args.getSerializable(KEY_CALENDAR_FOR_DISPLAY);
//            }
//        }
//        if (calendarForDisplay == null) {
//            calendarForDisplay = Calendar.getInstance(); // Use current date if we have nothing else
//        }

        int year = calendarForDisplay.get(Calendar.YEAR);
        int month = calendarForDisplay.get(Calendar.MONTH);
        int day = calendarForDisplay.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putSerializable(KEY_CALENDAR_FOR_DISPLAY, calendarForDisplay);
//        super.onSaveInstanceState(savedInstanceState);
//    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendarForDisplay.set(year, month, dayOfMonth);
//        try {
//            if (tripDateSelectedListener == null) {
//                Log.e(TAG, "TripDateSelectedListener is null");
//                return;
//            }
//            tripDateSelectedListener.onTripDateSelected(year, month, dayOfMonth, this.getTag()); // TODO: see to-do in handler method about param type
//        } catch (ClassCastException e) {
//            Log.e(TAG, "Containing fragment must implement DatePickerFragment.TripDateSelectedListener");
//            throw e;
//        }
    }

//    public void setTripDateSelectedListener(TripDateSelectedListener listener) {
//        this.tripDateSelectedListener = listener;
//    }
//
//    public interface TripDateSelectedListener {
//        void onTripDateSelected(int year, int month, int dayOfMonth, String tag);
//    }
}
