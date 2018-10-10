package ericrybarczyk.me.roadtrippy.endpoints;

import android.content.Intent;
import android.net.Uri;

import ericrybarczyk.me.roadtrippy.viewmodels.TripLocationViewModel;

public class NavigationIntentService {
    public static Intent getNavigationIntent(TripLocationViewModel tripLocationViewModel) {
        String destination = tripLocationViewModel.getLatitude() + "," + tripLocationViewModel.getLongitude();
        String uri = "https://www.google.com/maps/dir/?api=1&destination=" + destination + "&travelmode=driving&dir_action=navigate";
        Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        return mapIntent;
    }
}
