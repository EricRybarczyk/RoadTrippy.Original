package ericrybarczyk.me.roadtrippy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import ericrybarczyk.me.roadtrippy.dto.TripDay;
import ericrybarczyk.me.roadtrippy.engine.TripManager;
import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.util.InputUtils;
import ericrybarczyk.me.roadtrippy.util.RequestCodes;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;


public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    MapDisplayRequestListener,
                    GoogleMapFragment.LocationSelectedListener,
                    FragmentNavigationRequestListener,
                    TripManager.TripSaveRequestListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_ACTIVE_FRAGMENT_TAG = "active_fragment_tag";

    public static final String ANONYMOUS = "anonymous";
    private String activeUsername = ANONYMOUS;

    private String activeFragmentTag;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TripViewModel tripViewModel;

    @BindView(R.id.toolbar) protected Toolbar toolbar;
    @BindView(R.id.drawer_layout) protected DrawerLayout drawer;
    @BindView(R.id.nav_view) protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        verifyPermissions();
        updateLastKnownLocation();

        tripViewModel = ViewModelProviders.of(this).get(TripViewModel.class);

        firebaseAuth = FirebaseAuth.getInstance();

        /* SOURCE: FirebaseAuth code is directly adapted from Udacity & Google materials,
           including Udacity's Firebase extracurricular module in the Android Developer Nanodegree program,
           and https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md */
        authStateListener = firebaseAuth -> {
            firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                // TODO: look at this https://stackoverflow.com/questions/32806735/refresh-header-in-navigation-drawer/35952939#35952939 and consider different spot to set this username textview
                View header = navigationView.getHeaderView(0);
                activeUsername = firebaseUser.getDisplayName();
                TextView usernameText = header.findViewById(R.id.username_display_text);
                usernameText.setText(activeUsername);
                onSignedInInitialize(activeUsername);

            } else {

                onSignedOutCleanup();

                // configure supported sign-in providers
                List<AuthUI.IdpConfig> providers = Collections.singletonList(
                        new AuthUI.IdpConfig.GoogleBuilder().build());
                        // if I decide to add Email sign-in option:
                        //                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                        //                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                        //                            new AuthUI.IdpConfig.EmailBuilder().build());

                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false) // TODO: set true. Udacity tutorial set this to false (default is true: system will basically keep user automatically logged in)
                                .setAvailableProviders(providers)
                                .build(),
                        RequestCodes.SIGN_IN_REQUEST_CODE);
            }
        };

        // load initial fragment
        loadFragment(getFragmentInstance(FragmentTags.TAG_TRIP_LIST), FragmentTags.TAG_TRIP_LIST);
    }


    @Override
    public void onTripSaveRequest() {
        TripManager tripManager = new TripManager();
        Trip trip = tripManager.buildTrip(tripViewModel, firebaseUser.getUid());
        List<TripDay> tripDays = tripManager.buildInitialTripDays(tripViewModel);
        TripRepository repository = new TripRepository();
        repository.saveTrip(trip, tripDays);
        // reset the state of the ViewModel to default
        tripViewModel.reset();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) { // TODO: decide what I want to really do here. And maybe snackbar instead of toast?
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void onSignedInInitialize(String username) {
        this.activeUsername = username;
    }

    private void onSignedOutCleanup() {
        this.activeUsername = ANONYMOUS;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(KEY_ACTIVE_FRAGMENT_TAG, activeFragmentTag);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState); // call super first to restore view hierarchy
        activeFragmentTag = savedInstanceState.getString(KEY_ACTIVE_FRAGMENT_TAG, FragmentTags.TAG_TRIP_LIST);
        Fragment fragment = getFragmentInstance(activeFragmentTag);
        loadFragment(fragment, activeFragmentTag);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: determine if I will use the options menu and adjust accordingly. Maybe just for log-out?
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Fragment preferenceFragment = getFragmentInstance(FragmentTags.TAG_SETTINGS_PREFERENCES);
                loadFragment(preferenceFragment, FragmentTags.TAG_SETTINGS_PREFERENCES);
                return true;
            case R.id.action_sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation clicks by loading the appropriate fragment
        Fragment fragment;
        String fragmentTag = null;

        // TODO: support all fragments here
        switch (item.getItemId()) {
            case R.id.nav_trip_plans:
                fragmentTag = FragmentTags.TAG_TRIP_LIST;
                break;
            case R.id.nav_create_trip:
                fragmentTag = FragmentTags.TAG_CREATE_TRIP;
                break;
            case R.id.nav_trip_ideas:

                break;
            case R.id.nav_trip_history:

                break;
            case R.id.nav_settings:

                break;
            default:
                fragmentTag = FragmentTags.TAG_TRIP_LIST;
                break;
        }

        fragment = getFragmentInstance(fragmentTag);
        loadFragment(fragment, fragmentTag);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Fragment getFragmentInstance(String fragmentTag) {
        Class fragmentClass;
        Fragment result = null;

        // first check if Fragment has already been initialized, such as following configuration change
        result = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (result != null) {
            return result;
        }

        switch (fragmentTag) {
            case FragmentTags.TAG_TRIP_LIST:
                fragmentClass = TripListFragment.class;
                break;
            case FragmentTags.TAG_CREATE_TRIP:
                fragmentClass = CreateTripFragment.class;
                break;
            case FragmentTags.TAG_TRIP_OVERVIEW_MAP:
                fragmentClass = TripOverviewMapFragment.class;
                break;
            case FragmentTags.TAG_TRIP_DETAIL:
                fragmentClass = TripDetailFragment.class;
                break;
            case FragmentTags.TAG_SETTINGS_PREFERENCES:
                fragmentClass = SettingsFragment.class;
                break;
            // TODO: support all fragments here

            default:
                fragmentClass = TripListFragment.class;
                break;
        }
        try {
            result = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            Log.e(TAG, "Unable to instantiate instance of " + fragmentClass.getSimpleName() + " : " + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Illegal Access on instance of " + fragmentClass.getSimpleName() + " : " + e.getMessage());
        }

        return result;
    }

    private void loadFragment(Fragment fragment, String fragmentTag) {
        InputUtils.hideKeyboard(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, fragment, fragmentTag)
                .addToBackStack(null)
                .commit();
        activeFragmentTag = fragmentTag;
    }

    @Override
    public void onMapDisplayRequested(int requestCode, String returnToFragmentTag) {
        Fragment fragment = GoogleMapFragment.newInstance(lastKnownLocation, requestCode, returnToFragmentTag);
        loadFragment(fragment, FragmentTags.TAG_MAP_SELECT_LOCATION);
    }

    @Override
    public void onLocationSelected(LatLng location, int requestCode, String locationDescription) {
        switch (requestCode) {
            case RequestCodes.TRIP_ORIGIN_REQUEST_CODE:
                tripViewModel.setOriginLatLng(location);
                tripViewModel.setOriginDescription(locationDescription);
                break;
            case RequestCodes.TRIP_DESTINATION_REQUEST_CODE:
                tripViewModel.setDestinationLatLng(location);
                tripViewModel.setDestinationDescription(locationDescription);
                break;
            default:
                throw new IllegalArgumentException("Invalid requestCode argument: " + String.valueOf(requestCode));
        }
    }


    private void verifyPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted - request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    RequestCodes.LOCATION_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCodes.LOCATION_PERMISSIONS_REQUEST_CODE: {
                updateLastKnownLocation();
                break;
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLastKnownLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        lastKnownLocation = location;
                    }

                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "fusedLocationProviderClient onFailure: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onFragmentNavigationRequest(String fragmentTag) {
        Fragment fragment = getFragmentInstance(fragmentTag);
        loadFragment(fragment, fragmentTag);
    }

    @Override
    public void onFragmentNavigationRequest(String fragmentTag, String tripId) {
        Fragment fragment = getFragmentInstance(fragmentTag);
        Bundle args = new Bundle();
        args.putString(TripDetailFragment.KEY_TRIP_ID, tripId);
        fragment.setArguments(args);
        loadFragment(fragment, fragmentTag);
    }
}
