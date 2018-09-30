package ericrybarczyk.me.roadtrippy;

/*  Non-plagiarism statement: this class is directly adapted from
 *  https://developer.android.com/guide/topics/ui/controls/pickers#DatePicker
 */

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import org.threeten.bp.LocalDate;

import java.util.Calendar;

import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;

public class DatePickerFragment extends DialogFragment
        implements  DatePickerDialog.OnDateSetListener {

    private TripDateSelectedListener tripDateSelectedListener;
    private static final String TAG = DatePickerFragment.class.getSimpleName();
    //private Calendar calendarForDisplay = null;
    private LocalDate date;
    static final String KEY_CALENDAR_FOR_DISPLAY = "calendar_for_display";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        TripViewModel tripViewModel = ViewModelProviders.of(getActivity()).get(TripViewModel.class);

        assert getTag() != null;
        switch (getTag()) {
            case FragmentTags.TAG_DEPARTURE_DATE_DIALOG:
                date = tripViewModel.getDepartureDate();
                break;
            case FragmentTags.TAG_RETURN_DATE_DIALOG:
                if (tripViewModel.getReturnDate().compareTo(tripViewModel.getDepartureDate()) < 1) {
                    date = tripViewModel.getDepartureDate().plusDays(1);
                } else {
                    date = tripViewModel.getReturnDate();
                }
                break;
        }

        int year = date.getYear();
        int month = date.getMonthValue() - 1; // DatePickerDialog uses 0-based Month so decrement from LocalDate which uses 1-based Month
        int day = date.getDayOfMonth();

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tripDateSelectedListener = null;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // DatePicker uses 0-based month so +1 for org.threeten.bp.LocalDate
        int localDateMonth = month + 1;
        date = LocalDate.of(year, localDateMonth, dayOfMonth);
        try {
            if (tripDateSelectedListener == null) {
                Log.e(TAG, "TripDateSelectedListener is null");
                return;
            }
            tripDateSelectedListener.onTripDateSelected(year, localDateMonth, dayOfMonth, this.getTag()); // TODO: see to-do in handler method about param type
        } catch (ClassCastException e) {
            Log.e(TAG, "Containing fragment must implement DatePickerFragment.TripDateSelectedListener");
            throw e;
        }
    }

    public void setTripDateSelectedListener(TripDateSelectedListener listener) {
        this.tripDateSelectedListener = listener;
    }

    public interface TripDateSelectedListener {
        void onTripDateSelected(int year, int month, int dayOfMonth, String tag);
    }
}
