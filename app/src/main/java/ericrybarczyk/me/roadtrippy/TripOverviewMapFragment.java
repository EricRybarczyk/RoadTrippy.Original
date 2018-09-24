package ericrybarczyk.me.roadtrippy;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.directions.DirectionsResponse;
import ericrybarczyk.me.roadtrippy.directions.Route;
import ericrybarczyk.me.roadtrippy.endpoints.GetDirectionsEndpoint;
import ericrybarczyk.me.roadtrippy.endpoints.SearchService;
import ericrybarczyk.me.roadtrippy.engine.TripManager;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripOverviewMapFragment extends Fragment implements OnMapReadyCallback {

    private TripViewModel tripViewModel;
    private SupportMapFragment mapFragment;
    private String googleMapsApiKey;
    private GoogleMap googleMap;
    private FragmentNavigationRequestListener fragmentNavigationRequestListener;
    private TripManager.TripSaveRequestListener tripSaveRequestListener;
    private static final String TAG = TripOverviewMapFragment.class.getSimpleName();

    @BindView(R.id.heading_create_trip) protected TextView headingText;
    @BindView(R.id.trip_confirm_button) protected Button tripConfirmButton;

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

        headingText.setText(tripViewModel.getDescription());

        tripConfirmButton.setOnClickListener(v -> {
            saveTrip();
            fragmentNavigationRequestListener.onFragmentNavigationRequest(FragmentTags.TAG_TRIP_LIST);
        });

        mapFragment.getMapAsync(this);

        return rootView;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMyLocationEnabled(true);
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);

        // get Directions for the trip origin to destination to show user the route overview
        String origin = tripViewModel.getOriginLatLng().latitude + "," + tripViewModel.getOriginLatLng().longitude;
        String destination = tripViewModel.getDestinationLatLng().latitude + "," + tripViewModel.getDestinationLatLng().longitude;

        GetDirectionsEndpoint endpoint = SearchService.getClient().create(GetDirectionsEndpoint.class);
        Call<DirectionsResponse> directionsCall = endpoint.getDirections(googleMapsApiKey, origin, destination);
        directionsCall.enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                DirectionsResponse directionsResponse = response.body();

                // Convert overview_polyline into list of LatLng and add as Polyline
                if ((directionsResponse != null && directionsResponse.getRoutes() != null ? directionsResponse.getRoutes().size() : 0) > 0) {

                    // use the first route for simple overview. Actual navigation will be in Google Maps and user can make changes
                    Route route = directionsResponse.getRoutes().get(0);

                    // add a marker at the starting & ending points
                    LatLng start = new LatLng(route.getLegs().get(0).getStartLocation().getLat(), route.getLegs().get(0).getStartLocation().getLng());
                    LatLng end = new LatLng(route.getLegs().get(0).getEndLocation().getLat(), route.getLegs().get(0).getEndLocation().getLng());
                    googleMap.addMarker(new MarkerOptions().position(start));
                    googleMap.addMarker(new MarkerOptions().position(end));

                    // get the polyline to show
                    String encodedPolyline = route.getOverviewPolyline().getPoints();
                    List<LatLng> overviewPoints = PolyUtil.decode(encodedPolyline);

                    // source for TypedValue technique: https://stackoverflow.com/a/8780360/798642
                    TypedValue widthValue = new TypedValue();
                    TypedValue offsetValue = new TypedValue();
                    getResources().getValue(R.dimen.map_polyline_width, widthValue, true);
                    getResources().getValue(R.dimen.map_bounds_expand_amount, offsetValue, true);
                    float width = widthValue.getFloat();
                    float offset = offsetValue.getFloat();

                    // Polyline overviewPolyline =
                    googleMap.addPolyline(new PolylineOptions()
                            .clickable(false)
                            .color(getResources().getColor(R.color.colorMapPolyline))
                            .width(width)
                            .addAll(overviewPoints)
                            );

                    // get the bounds for the map view, expand by offset to make it all fit in view
                    LatLngBounds overviewBounds = new LatLngBounds(
                            new LatLng(route.getBounds().getSouthwest().getLat() - offset, route.getBounds().getSouthwest().getLng() - offset),
                            new LatLng(route.getBounds().getNortheast().getLat() + offset, route.getBounds().getNortheast().getLng() + offset)
                    );
                    // Set the camera to the greatest possible zoom level that includes the bounds
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(overviewBounds, 0));

                    // another option: center and set zoom level
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(overviewBounds.getCenter(), 5));

                } else {
                    Log.d(TAG, "Retrofit onResponse: No Directions search result.");
                    Toast.makeText(getContext(), R.string.map_directions_failure, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e(TAG, "Retrofit Callback onFailure: " + t.getMessage());
                Toast.makeText(getActivity(), R.string.map_directions_call_error_message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveTrip() {
        tripSaveRequestListener.onTripSaveRequest();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigationRequestListener) {
            fragmentNavigationRequestListener = (FragmentNavigationRequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentNavigationRequestListener");
        }
        if (context instanceof TripManager.TripSaveRequestListener) {
            tripSaveRequestListener = (TripManager.TripSaveRequestListener) context;
        }else {
            throw new RuntimeException(context.toString() + " must implement TripManager.TripSaveRequestListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentNavigationRequestListener = null;
        tripSaveRequestListener = null;
    }
}
