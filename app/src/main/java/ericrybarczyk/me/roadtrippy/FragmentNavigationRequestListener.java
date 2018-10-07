package ericrybarczyk.me.roadtrippy;

import android.support.v4.app.Fragment;

public interface FragmentNavigationRequestListener {
    void onFragmentNavigationRequest(String fragmentTag);

    // TODO: see about refactoring this to a clearer name, like the one below
    void onFragmentNavigationRequest(String fragmentTag, String tripId, String tripDescription);

    void onTripDayEditFragmentRequest(String fragmentTag, String tripId, int dayNumber, String nodeKey);
}
