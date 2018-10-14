package ericrybarczyk.me.roadtrippy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.util.ArgumentKeys;

public class TripOriginPickerFragment extends DialogFragment  {

    @BindView(R.id.home_origin_button) protected Button homeOriginButton;
    @BindView(R.id.pick_origin_button) protected Button pickOriginButton;

    private TripOriginSelectedListener listener;


    public TripOriginPickerFragment() {
    }

    public static TripOriginPickerFragment newInstance(TripOriginSelectedListener listener) {
        TripOriginPickerFragment pickerFragment = new TripOriginPickerFragment();
        pickerFragment.setTripOriginSelectedListener(listener);
        pickerFragment.setStyle(STYLE_NO_TITLE, 0);
        return pickerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.dialog_select_trip_origin, container);
        ButterKnife.bind(this, rootView);

        homeOriginButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTripOriginSelected(ArgumentKeys.KEY_HOME_ORIGIN);
                this.dismiss();
            }
        });
        pickOriginButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTripOriginSelected(ArgumentKeys.KEY_PICK_ORIGIN);
                this.dismiss();
            }
        });

        return rootView;
    }

    private void setTripOriginSelectedListener(TripOriginSelectedListener listener) {
        this.listener = listener;
    }
    public interface TripOriginSelectedListener {
        void onTripOriginSelected(String key);
    }

}
