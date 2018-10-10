package ericrybarczyk.me.roadtrippy;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class TripDayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private OnTripDayListClickListener onTripDayListClickListener;
    private OnNavigationClickListener onNavigationClickListener;
    @BindView(R.id.day_number) protected TextView dayNumber;
    @BindView(R.id.day_primary_description) protected TextView dayPrimaryDescription;
    @BindView(R.id.day_user_notes) protected TextView dayUserNotes;
    @BindView(R.id.icon_navigate_trip_day) protected ImageView iconNavigation;


    private static final String TAG = TripDayViewHolder.class.getSimpleName();

    public TripDayViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setTripDayListClickListener(OnTripDayListClickListener listClickistener) {
        onTripDayListClickListener = listClickistener;
    }
    public void setNavigationClickListener(OnNavigationClickListener navigationListener) {
        onNavigationClickListener = navigationListener;
    }


    @Override
    @OnClick
    public void onClick(View v) {
        if (onTripDayListClickListener == null) {
            Log.e(TAG, "onTripDayListClickListener is null");
            return;
        }
        onTripDayListClickListener.onTripDayListItemClick();
    }

    @OnClick(R.id.icon_navigate_trip_day)
    public void onNavigationIconClick() {
        onNavigationClickListener.onNavigationClick();
    }

    interface OnTripDayListClickListener {
        void onTripDayListItemClick();
    }

    interface OnNavigationClickListener {
        void onNavigationClick();
    }


}
