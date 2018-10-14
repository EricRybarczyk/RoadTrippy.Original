package ericrybarczyk.me.roadtrippy;

public interface FragmentNavigationRequestListener {
    void onFragmentNavigationRequest(String fragmentTag);

    void onFragmentNavigationRequest(String fragmentTag, String tripId, String tripNodeKey, boolean isArchived);

    void onTripDayEditFragmentRequest(String fragmentTag, String tripId, String tripNodeKey, int dayNumber, String dayNodeKey);
}