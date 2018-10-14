package ericrybarczyk.me.roadtrippy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.endpoints.FindPlacesEndpoint;
import ericrybarczyk.me.roadtrippy.endpoints.SearchService;
import ericrybarczyk.me.roadtrippy.places.Candidate;
import ericrybarczyk.me.roadtrippy.places.PlacesResponse;
import ericrybarczyk.me.roadtrippy.util.ArgumentKeys;
import ericrybarczyk.me.roadtrippy.util.InputUtils;
import ericrybarczyk.me.roadtrippy.util.MapSettings;
import ericrybarczyk.me.roadtrippy.util.RequestCodes;
import ericrybarczyk.me.roadtrippy.viewmodels.TripDayViewModel;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class GoogleMapFragment extends Fragment
        implements  OnMapReadyCallback, GoogleMap.OnMapClickListener,
                    GoogleMap.OnCameraMoveStartedListener, View.OnClickListener {

    @BindView(R.id.search_button) protected Button searchButton;
    @BindView(R.id.set_location_button) protected Button setLocationButton;
    @BindView(R.id.description_text) protected EditText locationDescription;
    @BindView(R.id.search_text) protected EditText searchText;

    private TripViewModel tripViewModel;
    private TripDayViewModel tripDayViewModel;
    private SupportMapFragment mapFragment;
    private String googleMapsApiKey;
    private GoogleMap googleMap;

    private static final String TAG = GoogleMapFragment.class.getSimpleName();

    private LatLng mapLocation;
    private boolean displayForUserCurrentLocation;
    private CameraPosition cameraPosition;
    private float lastMapZoomLevel;
    private LocationSelectedListener locationSelectedListener;
    private int requestCode; // passed in from caller to be returned with map location
    private FragmentNavigationRequestListener fragmentNavigationRequestListener;
    private String returnFragmentTag;
    private String argumentLocationDescription;

    public GoogleMapFragment() {
    }

    public static GoogleMapFragment newInstance(int requestCode, String returnToFragmentTag) {
        GoogleMapFragment mapFragment = new GoogleMapFragment();
        Bundle args = new Bundle();
        args.putInt(ArgumentKeys.KEY_REQUEST_CODE, requestCode);
        args.putString(ArgumentKeys.KEY_RETURN_FRAGMENT_TAG, returnToFragmentTag);
        mapFragment.setArguments(args);
        return mapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleMapsApiKey = getString(R.string.google_maps_key);
        lastMapZoomLevel = MapSettings.MAP_DEFAULT_ZOOM;
        displayForUserCurrentLocation = true; // by default map will show users current location

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ArgumentKeys.KEY_START_LAT)) {
                requestCode = savedInstanceState.getInt(ArgumentKeys.KEY_REQUEST_CODE);
                returnFragmentTag = savedInstanceState.getString(ArgumentKeys.KEY_RETURN_FRAGMENT_TAG);
                lastMapZoomLevel = savedInstanceState.getFloat(ArgumentKeys.KEY_LAST_MAP_ZOOM_LEVEL);
            } else if (savedInstanceState.containsKey(MapSettings.KEY_MAP_DISPLAY_LATITUDE)) {
                displayForUserCurrentLocation = false; // flag request to show a requested location instead of user current location
                double latitude = (double) savedInstanceState.getFloat(MapSettings.KEY_MAP_DISPLAY_LATITUDE);
                double longitude = (double) savedInstanceState.getFloat(MapSettings.KEY_MAP_DISPLAY_LONGITUDE);
                mapLocation = new LatLng(latitude, longitude);
            }
        } else if (getArguments() != null) {
            requestCode = getArguments().getInt(ArgumentKeys.KEY_REQUEST_CODE);
            returnFragmentTag = getArguments().getString(ArgumentKeys.KEY_RETURN_FRAGMENT_TAG);
            if (getArguments().containsKey(MapSettings.KEY_MAP_DISPLAY_LATITUDE)) {
                displayForUserCurrentLocation = false; // flag request to show a requested location instead of user current location
                double latitude = (double) getArguments().getFloat(MapSettings.KEY_MAP_DISPLAY_LATITUDE);
                double longitude = (double) getArguments().getFloat(MapSettings.KEY_MAP_DISPLAY_LONGITUDE);
                mapLocation = new LatLng(latitude, longitude);
            }
            if (getArguments().containsKey(MapSettings.KEY_MAP_DISPLAY_LOCATION_DESCRIPTION)) {
                argumentLocationDescription = getArguments().getString(MapSettings.KEY_MAP_DISPLAY_LOCATION_DESCRIPTION);
            }
        }

        tripViewModel = ViewModelProviders.of(getActivity()).get(TripViewModel.class);
        tripDayViewModel = ViewModelProviders.of(getActivity()).get(TripDayViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_google_map, container, false);
        ButterKnife.bind(this, rootView);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        searchButton.setOnClickListener(this);

        if (requestCode == RequestCodes.PREFERENCE_HOME_LOCATION_REQUEST_CODE) {
            locationDescription.setText(getString(R.string.word_for_HOME));
            setLocationButton.setText(getString(R.string.map_home_location_button_save_label));
        }

        if (argumentLocationDescription != null) {
            locationDescription.setText(argumentLocationDescription);
            argumentLocationDescription = null; // clear this out so it doesn't persist if the fragment is loaded again
        }

        setLocationButton.setOnClickListener(v -> {
            if (v.getId() == setLocationButton.getId()) {
                if (requestCode == RequestCodes.TRIP_DESTINATION_REQUEST_CODE) {
                    updateMapView(MapSettings.MAP_SEARCH_RESULT_ZOOM); // make sure the map is displayed in a way that works well for the snapshot
                    // save a bitmap of the Google Map
                    // code based on https://stackoverflow.com/a/26946907/798642 and https://stackoverflow.com/a/17674787/798642
                    googleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(Bitmap bitmap) {
                            saveMapSnapshotImage(bitmap);
                        }
                    });
                }
                InputUtils.hideKeyboardFrom(getContext(), searchText);

                if (requestCode == RequestCodes.TRIP_DAY_DESTINATION_REQUEST_CODE) {
                    locationSelectedListener.onTripDayDestinationSelected(mapLocation, requestCode, locationDescription.getText().toString());
                } else {
                    locationSelectedListener.onLocationSelected(mapLocation, requestCode, locationDescription.getText().toString());
                }

                fragmentNavigationRequestListener.onFragmentNavigationRequest(returnFragmentTag);
            }
        });

        int permission = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PERMISSION_GRANTED) {
            updateLocation();
        } else {
            // Permission is not granted - request the permission
            requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                                RequestCodes.LOCATION_PERMISSIONS_REQUEST_CODE);
        }

        rootView.clearFocus();
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCodes.LOCATION_PERMISSIONS_REQUEST_CODE: {
                updateLocation();
                break;
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
        }
    }

    private void saveMapSnapshotImage(Bitmap bitmap) {
        File imageDir = getContext().getDir(MapSettings.DESTINATION_MAP_IMAGE_DIRECTORY, Context.MODE_PRIVATE);

        // slice a part of the image for use in trip list view
        int currentWidth = bitmap.getWidth();
        int currentHeight = bitmap.getHeight();
        // original is slightly portrait. Remove 20% horizontal 60% vertical, keep centered remainder. Looks good in List View, Picasso centers to fit.
        int startX = Math.round(currentWidth * 0.1f);
        int startY = Math.round(currentHeight * 0.3f);
        int resizedWidth = currentWidth - (startX * 2);
        int resizedHeight = currentHeight - (startY * 2);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, startX, startY, resizedWidth, resizedHeight);

        // save the main image now
        File mainFile = new File(imageDir, MapSettings.DESTINATION_MAP_MAIN_PREFIX + tripViewModel.getTripId() + MapSettings.DESTINATION_MAP_IMAGE_EXTENSION);
        File resizedFile = new File(imageDir, MapSettings.DESTINATION_MAP_SLICED_PREFIX + tripViewModel.getTripId() + MapSettings.DESTINATION_MAP_IMAGE_EXTENSION);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mainFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, MapSettings.MAP_IMAGE_SAVE_QUALITY, fos);
            fos = new FileOutputStream(resizedFile);
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, MapSettings.MAP_IMAGE_SAVE_QUALITY, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    private void updateLocation() {
        LocationServices.getFusedLocationProviderClient(getContext())
                .getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (displayForUserCurrentLocation) {
                            mapLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                        mapFragment.getMapAsync(GoogleMapFragment.this);
                    }
                });
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
        updateMapView(MapSettings.MAP_CLICK_ZOOM);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        InputUtils.hideKeyboardFrom(getContext(), getView());
    }

    private void updateMapView() {
        updateMapView(lastMapZoomLevel);
    }

    private void updateMapView(float zoomLevel) {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(mapLocation));
        cameraPosition = new CameraPosition.Builder().target(mapLocation)
                .zoom(zoomLevel)
                .bearing(MapSettings.MAP_DEFAULT_BEARING)
                .tilt(MapSettings.MAP_DEFAULT_TILT)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(cameraUpdate);
        lastMapZoomLevel = zoomLevel;
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
                            updateMapView(MapSettings.MAP_SEARCH_RESULT_ZOOM);
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
        savedInstanceState.putInt(ArgumentKeys.KEY_REQUEST_CODE, requestCode);
        savedInstanceState.putString(ArgumentKeys.KEY_RETURN_FRAGMENT_TAG, returnFragmentTag);
        savedInstanceState.putFloat(ArgumentKeys.KEY_LAST_MAP_ZOOM_LEVEL, lastMapZoomLevel);
        savedInstanceState.putFloat(MapSettings.KEY_MAP_DISPLAY_LATITUDE, (float)mapLocation.latitude);
        savedInstanceState.putFloat(MapSettings.KEY_MAP_DISPLAY_LONGITUDE, (float)mapLocation.longitude);
        super.onSaveInstanceState(savedInstanceState);
    }
    public interface LocationSelectedListener {
        void onLocationSelected(LatLng location, int requestCode, String locationDescription);
        void onTripDayDestinationSelected(LatLng location, int requestCode, String locationDescription); // , Bundle args
    }

}
