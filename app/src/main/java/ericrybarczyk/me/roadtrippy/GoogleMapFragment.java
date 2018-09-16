package ericrybarczyk.me.roadtrippy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.instructions_text) protected TextView instructionsText;
//    @BindView(R.id.map) protected MapFragment mapFragment;
    private SupportMapFragment mapFragment;

    private String googleMapsApiKey;
    private GoogleMap googleMap;

    public static final String KEY_START_LAT = "start_location_latitude";
    public static final String KEY_START_LNG = "start_location_longitude";

    private static final LatLng DEMO_START_LAT_LNG = new LatLng(36.375148, -94.207480); // 36.375148, -94.207480 | 36.207002, -86.690697
    private static final CameraPosition DEMO_START_CAMERA_POSITION =
            new CameraPosition.Builder().target(DEMO_START_LAT_LNG)
                    .zoom(12.0f)
                    .bearing(360f)
                    .tilt(0f)
                    .build();

    public GoogleMapFragment() {
    }

    public static GoogleMapFragment newInstance() {
        GoogleMapFragment mapFragment = new GoogleMapFragment();
        Bundle args = new Bundle();
        args.putDouble(KEY_START_LAT, DEMO_START_LAT_LNG.latitude);
        args.putDouble(KEY_START_LNG, DEMO_START_LAT_LNG.longitude);
        mapFragment.setArguments(args);
        return mapFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_google_map, container, false);
        ButterKnife.bind(this, rootView);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        googleMapsApiKey = getString(R.string.google_maps_key);
        mapFragment.getMapAsync(this);


        rootView.clearFocus(); // TODO: test if this helps prevent showing keyboard when app is opened from background
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        googleMap.addMarker(new MarkerOptions().position(DEMO_START_LAT_LNG)); // arbitrary, just show something at app start
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(DEMO_START_CAMERA_POSITION);
        map.animateCamera(cameraUpdate);
    }


    // TODO: save instance state
}
