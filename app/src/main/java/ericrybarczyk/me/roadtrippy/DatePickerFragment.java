package ericrybarczyk.me.roadtrippy;

/*  Non-plagiarism statement: this class is directly adapted from
 *  https://developer.android.com/guide/topics/ui/controls/pickers#DatePicker
 */

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment
        implements  DatePickerDialog.OnDateSetListener {

    private TripDateSelectedListener tripDateSelectedListener;
    private GregorianCalendar departureDate, returnDate;
    private static final String TAG = DatePickerFragment.class.getSimpleName();


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // TODO - save the date
        try {
//            TripDateSelectedListener listener = (TripDateSelectedListener) getTargetFragment();
            if (tripDateSelectedListener == null) {
                Log.e(TAG, "TripDateSelectedListener is null");
                return;
            }
            tripDateSelectedListener.onTripDateSelected(year, month, dayOfMonth, this.getTag());
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
