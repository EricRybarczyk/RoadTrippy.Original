package ericrybarczyk.me.roadtrippy;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NavigationDestinationHolder extends RecyclerView.ViewHolder {

    private double destinationLatitude;
    private double destinationLongitude;

    @BindView(R.id.navigate_to_destination_button) protected Button navigateButton;

    public NavigationDestinationHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }
}