package ericrybarczyk.me.roadtrippy;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.Toast;

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
import ericrybarczyk.me.roadtrippy.endpoints.FindPlacesEndpoint;
import ericrybarczyk.me.roadtrippy.endpoints.SearchService;
import ericrybarczyk.me.roadtrippy.places.Candidate;
import ericrybarczyk.me.roadtrippy.places.PlacesResponse;
import ericrybarczyk.me.roadtrippy.util.InputUtils;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleMapFragment extends Fragment
        implements  OnMapReadyCallback, GoogleMap.OnMapClickListener,
                    GoogleMap.OnCameraMoveStartedListener, View.OnClickListener {

    @BindView(R.id.search_button) protected Button searchButton;
    @BindView(R.id.set_location_button) protected Button setLocationButton;
    @BindView(R.id.description_text) protected EditText locationDescription;
    @BindView(R.id.search_text) protected EditText searchText;

    private TripViewModel tripViewModel;
    private SupportMapFragment mapFragment;
    private String googleMapsApiKey;
    private GoogleMap googleMap;

    private static final float MAP_DEFAULT_ZOOM = 12.0f;
    private static final float MAP_DEFAULT_BEARING = 360.0f;
    private static final float MAP_DEFAULT_TILT = 0.0f;

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

    public static GoogleMapFragment newInstance(Location initialLocation, int requestCode, String returnToFragmentTag) {
        GoogleMapFragment mapFragment = new GoogleMapFragment();
        Bundle args = new Bundle();
        args.putDouble(KEY_START_LAT, initialLocation.getLatitude());
        args.putDouble(KEY_START_LNG, initialLocation.getLongitude());
        args.putInt(KEY_REQUEST_CODE, requestCode);
        args.putString(KEY_RETURN_FRAGMENT_TAG, returnToFragmentTag);
        mapFragment.setArguments(args);
        return mapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleMapsApiKey = getString(R.string.google_maps_key);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_START_LAT)) {
                mapLocation = new LatLng(savedInstanceState.getDouble(KEY_START_LAT), savedInstanceState.getDouble(KEY_START_LNG));
                requestCode = savedInstanceState.getInt(KEY_REQUEST_CODE);
                returnFragmentTag = savedInstanceState.getString(KEY_RETURN_FRAGMENT_TAG);
            }
        } else if (getArguments() != null) {
            mapLocation = new LatLng(getArguments().getDouble(KEY_START_LAT), getArguments().getDouble(KEY_START_LNG));
            requestCode = getArguments().getInt(KEY_REQUEST_CODE);
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
        searchButton.setOnClickListener(this);

        setLocationButton.setOnClickListener(v -> {
            if (v.getId() == setLocationButton.getId()) {
                locationSelectedListener.onLocationSelected(mapLocation, requestCode, locationDescription.getText().toString());
                fragmentNavigationRequestListener.onFragmentNavigationRequest(returnFragmentTag);
            }
        });

        rootView.clearFocus();

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
        if (context instanceof LocationSelectedListener) {
            locationSelectedListener = (LocationSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LocationSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentNavigationRequestListener = null;
        locationSelectedListener = null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMapClickListener(this);
        googleMap.setMyLocationEnabled(true);

        UiSettings uiSettings = googleMap.getUiSettings();
        //uiSettings.setMyLocationButtonEnabled(true); // when something is selected on map, this shows two Maps Intents button icons (Directions, Map)
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);

        updateMapView();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mapLocation = latLng;
        updateMapView();
    }

    @Override
    public void onCameraMoveStarted(int i) {
        InputUtils.hideKeyboardFrom(getContext(), getView());
//        if (i == REASON_GESTURE) {
//        }
    }


    private void updateMapView() {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(mapLocation));
        cameraPosition = new CameraPosition.Builder().target(mapLocation)
                .zoom(MAP_DEFAULT_ZOOM)
                .bearing(MAP_DEFAULT_BEARING)
                .tilt(MAP_DEFAULT_TILT)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == searchButton.getId()) {
            String searchValue;
            if (searchText.getText() == null) { return; }
            searchValue = searchText.getText().toString();

            InputUtils.hideKeyboardFrom(getContext(), getView());
            FindPlacesEndpoint endpoint = SearchService.getClient().create(FindPlacesEndpoint.class);
            Call<PlacesResponse> findPlacesCall = endpoint.findPlaces(googleMapsApiKey, SearchService.PLACES_API_TEXT_QUERY_INPUT_TYPE, searchValue, SearchService.PLACES_API_QUERY_FIELDS);

            findPlacesCall.enqueue(new Callback<PlacesResponse>() {
                @Override
                public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                    PlacesResponse placesResponse = response.body();
                    if ((placesResponse != null && placesResponse.getCandidates() != null ? placesResponse.getCandidates().size() : 0) > 0) {

                        // if one result, set that as the location and update UI
                        if (placesResponse.getCandidates().size() == 1) {
                            Candidate place = placesResponse.getCandidates().get(0);
                            mapLocation = new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());
                            locationDescription.setText(place.getName());
                            updateMapView();
                        } else {
                            Log.d(TAG, "Too many Places Search results. Result count = " + String.valueOf(placesResponse.getCandidates().size()));
                            Toast.makeText(getContext(), R.string.map_search_too_many_results_message, Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Log.d(TAG, "Retrofit onResponse: No Places Search results.");
                        Toast.makeText(getContext(), R.string.map_search_no_results_message, Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(Call<PlacesResponse> call, Throwable t) {
                    Log.e(TAG, "Failed to call Places API. Error: " + t.getMessage());
                    Toast.makeText(getContext(), R.string.map_search_call_error_message, Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putDouble(KEY_START_LAT, mapLocation.latitude);
        savedInstanceState.putDouble(KEY_START_LNG, mapLocation.longitude);
        savedInstanceState.putInt(KEY_REQUEST_CODE, requestCode);
        savedInstanceState.putString(KEY_RETURN_FRAGMENT_TAG, returnFragmentTag);
        super.onSaveInstanceState(savedInstanceState);
    }
    public interface LocationSelectedListener {
        void onLocationSelected(LatLng location, int requestCode, String locationDescription);
    }

}
