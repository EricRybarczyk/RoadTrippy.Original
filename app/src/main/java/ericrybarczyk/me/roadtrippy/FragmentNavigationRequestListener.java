package ericrybarczyk.me.roadtrippy;

import android.support.v4.app.Fragment;

public interface FragmentNavigationRequestListener {
    void onFragmentNavigationRequest(String fragmentTag);
    void onFragmentNavigationRequest(String fragmentTag, String tripId, String tripDescription);
}
