package ericrybarczyk.me.roadtrippy;

public interface FragmentNavigationRequestListener {
    void onFragmentNavigationRequest(String fragmentTag);

    // TODO: see about refactoring this to a clearer name, like the one below
    void onFragmentNavigationRequest(String fragmentTag, String tripId, String tripNodeKey, boolean isArchived);

    void onTripDayEditFragmentRequest(String fragmentTag, String tripId, String tripNodeKey, int dayNumber, String dayNodeKey);
}