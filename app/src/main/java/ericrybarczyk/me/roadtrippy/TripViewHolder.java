package ericrybarczyk.me.roadtrippy;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    OnTripListClickListener onTripListClickListener;
    private String tripId;

    @BindView(R.id.trip_name_text) protected TextView tripName;
    @BindView(R.id.trip_directions_overview_text) protected TextView tripDirectionsOverview;
    @BindView(R.id.trip_date_range_text) protected TextView tripDateRange;
    @BindView(R.id.trip_highlight_one_text) protected TextView highlightOne;
    @BindView(R.id.trip_highlight_two_text) protected TextView highlightTwo;
    @BindView(R.id.driving_duration_text) protected TextView drivingDuration;
    @BindView(R.id.icon_highlight_one) protected TextView iconHighlightOne;
    @BindView(R.id.icon_highlight_two) protected TextView iconHighlightTwo;

    private static final String TAG = TripViewHolder.class.getSimpleName();


    public TripViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void setTripId(String tripId) {
        this.tripId = tripId;
    }

    void setTripListClickListener(OnTripListClickListener listener) {
        onTripListClickListener = listener;
    }

    @Override
    @OnClick
    public void onClick(View v) {
        if (onTripListClickListener == null) {
            Log.e(TAG, "onTripListClickListener is null");
            return;
        }
        if (tripId == null) {
            Log.e(TAG, "String tripId is null");
            return;
        }
        onTripListClickListener.onTripListItemClick(tripId);
    }

    interface OnTripListClickListener {
        void onTripListItemClick(String tripId);
    }

}
