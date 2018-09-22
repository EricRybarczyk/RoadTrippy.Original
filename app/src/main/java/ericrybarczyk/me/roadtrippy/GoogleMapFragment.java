package ericrybarczyk.me.roadtrippy;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;

public class GoogleMapFragment extends Fragment
        implements  OnMapReadyCallback,
                    GoogleMap.OnMapClickListener {

    @BindView(R.id.instructions_text) protected TextView instructionsText;
    @BindView(R.id.set_location_button) protected Button setLocationButton;
    @BindView(R.id.description_text) protected EditText locationDescription;

    private TripViewModel tripViewModel;
    private SupportMapFragment mapFragment;
    private String googleMapsApiKey;
    private GoogleMap googleMap;

    public static final String KEY_START_LAT = "start_location_latitude";
    public static final String KEY_START_LNG = "start_location_longitude";
    public static final String KEY_REQUEST_CODE = "request_code_from_caller";
    public static final String KEY_RETURN_FRAGMENT_TAG = "return_fragment_tag";
    private static final String TAG = GoogleMapFragment.class.getSimpleName();

    private LatLng mapLocation;
    private CameraPosition cameraPosition;
    private LocationSelectedListener locationSelectedListener;
    private int requestCode; // passed in from caller to be returned with map location
    private FragmentNavigationRequestListener fragmentNavigationRequestListener;
    private String returnFragmentTag;

    public GoogleMapFragment() {
    }

    public static GoogleMapFragment newInstance(Location initialLocation, LocationSelectedListener listener, int requestCode, String returnToFragmentTag) {
        GoogleMapFragment mapFragment = new GoogleMapFragment();
        mapFragment.setLocationSelectedListener(listener);
        Bundle args = new Bundle();
        args.putDouble(KEY_START_LAT, initialLocation.getLatitude());
        args.putDouble(KEY_START_LNG, initialLocation.getLongitude());
        args.putInt(KEY_REQUEST_CODE, requestCode);
        args.putString(KEY_RETURN_FRAGMENT_TAG, returnToFragmentTag);
        mapFragment.setArguments(args);
        return mapFragment;
    }

    void setLocationSelectedListener(LocationSelectedListener listener) {
        this.locationSelectedListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleMapsApiKey = getString(R.string.google_maps_key);

        if (getArguments() != null) {
            requestCode = getArguments().getInt(KEY_REQUEST_CODE);
            mapLocation = new LatLng(getArguments().getDouble(KEY_START_LAT), getArguments().getDouble(KEY_START_LNG));
            returnFragmentTag = getArguments().getString(KEY_RETURN_FRAGMENT_TAG);
        }

        tripViewModel = ViewModelProviders.of(getActivity()).get(TripViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_google_map, container, false);
        ButterKnife.bind(this, rootView);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        setLocationButton.setOnClickListener(v -> {
            if (v.getId() == setLocationButton.getId()) {
                locationSelectedListener.onLocationSelected(mapLocation, requestCode, locationDescription.getText().toString());
                fragmentNavigationRequestListener.onFragmentNavigationRequest(returnFragmentTag);
            }
        });

        cameraPosition = new CameraPosition.Builder().target(mapLocation)
                        .zoom(12.0f)
                        .bearing(360f)
                        .tilt(0f)
                        .build();

        rootView.clearFocus(); // TODO: test if this helps prevent showing keyboard when app is opened from background

        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigationRequestListener) {
            fragmentNavigationRequestListener = (FragmentNavigationRequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentNavigationRequestListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentNavigationRequestListener = null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMapClickListener(this);
        googleMap.setMyLocationEnabled(true);

        googleMap.addMarker(new MarkerOptions().position(mapLocation));

        UiSettings uiSettings = googleMap.getUiSettings();
        //uiSettings.setMyLocationButtonEnabled(true); // when something is selected on map, this shows two Maps Intents button icons (Directions, Map)
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mapLocation = latLng;
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(mapLocation));
    }

    public interface LocationSelectedListener {
        void onLocationSelected(LatLng location, int requestCode, String locationDescription);
    }

}
