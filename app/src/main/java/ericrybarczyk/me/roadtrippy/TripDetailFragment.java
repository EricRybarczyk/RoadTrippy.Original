package ericrybarczyk.me.roadtrippy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripDetailFragment extends Fragment {

    public static final String KEY_TRIP_ID = "trip_id_key";

    private String tripId;

    @BindView(R.id.trip_detail_image) protected ImageView tripImage;
    @BindView(R.id.trip_description) protected TextView tripDescription;

    private static final String TAG = TripDetailFragment.class.getSimpleName();

    public TripDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(KEY_TRIP_ID)) {
                tripId = getArguments().getString(KEY_TRIP_ID);
            }
        }
        if (tripId == null) {
            Log.e(TAG, "tripId is null, must be in getArguments() for this fragment");
            return;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_trip_detail, container, false);
        ButterKnife.bind(this, rootView);

        tripDescription.setText(tripId);

        return rootView;
    }
}
