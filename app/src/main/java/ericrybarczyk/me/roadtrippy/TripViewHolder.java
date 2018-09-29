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
