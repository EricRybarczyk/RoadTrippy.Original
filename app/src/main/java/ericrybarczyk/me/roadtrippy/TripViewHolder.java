package ericrybarczyk.me.roadtrippy;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripViewHolder extends RecyclerView.ViewHolder   { //implements View.OnClickListener

    @BindView(R.id.trip_name_text) protected TextView tripName;
    @BindView(R.id.trip_directions_overview_text) protected TextView tripDirectionsOverview;
    @BindView(R.id.trip_date_range_text) protected TextView tripDateRange;
    @BindView(R.id.trip_highlight_one_text) protected TextView highlightOne;
    @BindView(R.id.trip_highlight_two_text) protected TextView highlightTwo;
    @BindView(R.id.driving_duration_text) protected TextView drivingDuration;
    @BindView(R.id.icon_highlight_one) protected TextView iconHighlightOne;
    @BindView(R.id.icon_highlight_two) protected TextView iconHighlightTwo;


    public TripViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

//    @Override
//    public void onClick(View v) {
//        int adapterPosition = getAdapterPosition();
//        String tripId = tripList.get(adapterPosition).getTripId();
//        onTripClickHandler.onClick(tripId);
//    }
}
