package ericrybarczyk.me.roadtrippy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TripListFragment extends Fragment {


    public TripListFragment() {
        // Required empty public constructor
    }

    public static TripListFragment newInstance(String param1, String param2) {
        TripListFragment fragment = new TripListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_trip_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFabDisplayRequestListener) {
//            fabDisplayRequestListener = (OnFabDisplayRequestListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement OnFabDisplayRequestListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        fabDisplayRequestListener = null;
    }

}
