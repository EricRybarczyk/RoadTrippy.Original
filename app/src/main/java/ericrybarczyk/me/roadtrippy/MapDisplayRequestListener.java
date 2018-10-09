package ericrybarczyk.me.roadtrippy;

import com.google.android.gms.maps.model.LatLng;

public interface MapDisplayRequestListener {
    void onMapDisplayRequested(int requestCode, String returnToFragmentTag);
    void onMapDisplayRequested(int requestCode, String returnToFragmentTag, LatLng displayLocation, String locationDescription);
}
