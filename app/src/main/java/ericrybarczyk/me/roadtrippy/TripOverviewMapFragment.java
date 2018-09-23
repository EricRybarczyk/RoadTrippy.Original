package ericrybarczyk.me.roadtrippy;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;

public class TripOverviewMapFragment extends Fragment {

    private TripViewModel tripViewModel;
    private SupportMapFragment mapFragment;
    private String googleMapsApiKey;
    private GoogleMap googleMap;


    public TripOverviewMapFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleMapsApiKey = getString(R.string.google_maps_key);

        tripViewModel = ViewModelProviders.of(getActivity()).get(TripViewModel.class);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_trip_overview_map, container, false);
        ButterKnife.bind(this, rootView);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);



        return rootView;
    }
}
