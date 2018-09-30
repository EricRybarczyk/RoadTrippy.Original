package ericrybarczyk.me.roadtrippy;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class TripDayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.day_number) protected TextView dayNumber;
    @BindView(R.id.day_primary_description) protected TextView dayPrimaryDescription;
    @BindView(R.id.day_secondary_description) protected TextView daySecondaryDescription;
    @BindView(R.id.icon_directions) protected ImageView iconDirections;

    public TripDayViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    @OnClick
    public void onClick(View v) {

    }

    interface OnTripDayListClickListener {
        void onTripDayListItemClick();
    }

}
