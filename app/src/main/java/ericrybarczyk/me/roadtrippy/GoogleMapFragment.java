package ericrybarczyk.me.roadtrippy;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoogleMapFragment extends Fragment
        implements  OnMapReadyCallback,
                    GoogleMap.OnMapClickListener{

    @BindView(R.id.instructions_text) protected TextView instructionsText;
    @BindView(R.id.set_location_button) protected Button setLocationButton;
    private SupportMapFragment mapFragment;

    private String googleMapsApiKey;
    private GoogleMap googleMap;

    public static final String KEY_START_LAT = "start_location_latitude";
    public static final String KEY_START_LNG = "start_location_longitude";
    public static final String KEY_REQUEST_CODE = "request_code_from_caller";
    private static final String TAG = GoogleMapFragment.class.getSimpleName();

    private LatLng mapLocation;
    private CameraPosition cameraPosition;
    private LocationSelectedListener locationSelectedListener;
    private int requestCode; // passed in from caller to be returned with map location


    public GoogleMapFragment() {
    }

    public static GoogleMapFragment newInstance(Location initialLocation, LocationSelectedListener listener, int requestCode) {
        GoogleMapFragment mapFragment = new GoogleMapFragment();
        mapFragment.setLocationSelectedListener(listener);
        Bundle args = new Bundle();
        args.putDouble(KEY_START_LAT, initialLocation.getLatitude());
        args.putDouble(KEY_START_LNG, initialLocation.getLongitude());
        args.putInt(KEY_REQUEST_CODE, requestCode);
        mapFragment.setArguments(args);
        return mapFragment;
    }

    void setLocationSelectedListener(LocationSelectedListener listener) {
        this.locationSelectedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_google_map, container, false);
        ButterKnife.bind(this, rootView);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        setLocationButton.setOnClickListener(v -> {
            if (v.getId() == setLocationButton.getId()) {
                locationSelectedListener.onLocationSelected(mapLocation, requestCode);
            }
        });

        googleMapsApiKey = getString(R.string.google_maps_key);

        if (this.getArguments() != null) {
            requestCode = getArguments().getInt(KEY_REQUEST_CODE);
            mapLocation = new LatLng(getArguments().getDouble(KEY_START_LAT), getArguments().getDouble(KEY_START_LNG));
            cameraPosition = new CameraPosition.Builder().target(mapLocation)
                            .zoom(12.0f)
                            .bearing(360f)
                            .tilt(0f)
                            .build();
            Log.i(TAG, "Map set to LatLng: " + mapLocation.toString());
        }

        rootView.clearFocus(); // TODO: test if this helps prevent showing keyboard when app is opened from background

        mapFragment.getMapAsync(this);

        return rootView;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMapClickListener(this);
        googleMap.setMyLocationEnabled(true);

        googleMap.addMarker(new MarkerOptions().position(mapLocation));

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true); // when something is selected on map, this shows two Maps Intents button icons (Directions, Map)
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }


    // TODO: save instance state


    @Override
    public void onMapClick(LatLng latLng) {
        this.mapLocation = latLng;
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(latLng));
    }


    public interface LocationSelectedListener {
        void onLocationSelected(LatLng location, int requestCode);
    }

}
