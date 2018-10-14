package ericrybarczyk.me.roadtrippy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.threeten.bp.LocalDate;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.dto.TripDay;
import ericrybarczyk.me.roadtrippy.engine.TripManager;
import ericrybarczyk.me.roadtrippy.persistence.PersistenceFormats;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;
import ericrybarczyk.me.roadtrippy.tasks.TripArchiver;
import ericrybarczyk.me.roadtrippy.tasks.UserInfoSave;
import ericrybarczyk.me.roadtrippy.util.ArgumentKeys;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.util.InputUtils;
import ericrybarczyk.me.roadtrippy.util.MapSettings;
import ericrybarczyk.me.roadtrippy.util.MenuCodes;
import ericrybarczyk.me.roadtrippy.util.NetworkChecker;
import ericrybarczyk.me.roadtrippy.util.RequestCodes;
import ericrybarczyk.me.roadtrippy.viewmodels.TripDayViewModel;
import ericrybarczyk.me.roadtrippy.viewmodels.TripLocationViewModel;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;

public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    MapDisplayRequestListener,
                    GoogleMapFragment.LocationSelectedListener,
                    FragmentNavigationRequestListener,
                    TripDetailFragment.TripDisplayCommunicationListener,
                    TripManager.TripSaveRequestListener,
                    SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String ANONYMOUS = "anonymous";
    private String activeUsername = ANONYMOUS;
    private String userId;
    private String activeFragmentTag;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TripViewModel tripViewModel;
    private TripDayViewModel tripDayViewModel;
    private int preferenceDrivingHours;
    private String activeDisplayedTripNodeKey;

    @BindView(R.id.toolbar) protected Toolbar toolbar;
    @BindView(R.id.drawer_layout) protected DrawerLayout drawer;
    @BindView(R.id.nav_view) protected NavigationView navigationView;
    @BindView(R.id.content_container) protected FrameLayout contentFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        verifyPermissions();
        updateLastKnownLocation();
        loadPreferences();

        tripViewModel = ViewModelProviders.of(this).get(TripViewModel.class);
        tripDayViewModel = ViewModelProviders.of(this).get(TripDayViewModel.class);

        firebaseAuth = FirebaseAuth.getInstance();

        /* SOURCE: FirebaseAuth code is directly adapted from Udacity & Google materials,
           including Udacity's Firebase extracurricular module in the Android Developer Nanodegree program,
           and https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md */
        authStateListener = firebaseAuth -> {
            firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                View header = navigationView.getHeaderView(0);
                activeUsername = firebaseUser.getDisplayName();
                userId = firebaseUser.getUid();
                TextView usernameText = header.findViewById(R.id.username_display_text);
                usernameText.setText(activeUsername);
                onSignedInInitialize(firebaseUser);

            } else {

                onSignedOutCleanup();

                // configure supported sign-in providers
                List<AuthUI.IdpConfig> providers = Collections.singletonList(
                        new AuthUI.IdpConfig.GoogleBuilder().build());

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

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleListener(), false);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FragmentTags.KEY_ACTIVE_FRAGMENT_TAG)) {
                activeFragmentTag = savedInstanceState.getString(FragmentTags.KEY_ACTIVE_FRAGMENT_TAG, FragmentTags.TAG_TRIP_LIST);
            }
        }

        // see if we got here from a widget
        Intent starter = getIntent();
        if (starter.hasExtra(ArgumentKeys.WIDGET_REQUEST_TRIP_ID)) {
            activeFragmentTag = FragmentTags.TAG_TRIP_DETAIL;
            String tripId = starter.getStringExtra(ArgumentKeys.WIDGET_REQUEST_TRIP_ID);
            String tripNodeKey = starter.getStringExtra(ArgumentKeys.WIDGET_REQUEST_TRIP_NODE_KEY);
            onFragmentNavigationRequest(activeFragmentTag, tripId, tripNodeKey, false);
            return;
        }

        if (activeFragmentTag == null) {
            activeFragmentTag = FragmentTags.TAG_TRIP_LIST;
        }
        // load initial fragment (no force on configuration change, allow existing Fragment to be restored by system)
        boolean forceLoadFragment = (savedInstanceState == null);
        loadFragment(getFragmentInstance(activeFragmentTag, forceLoadFragment), activeFragmentTag, forceLoadFragment);

    }

    private void loadPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int defaultHours = Integer.parseInt(getString(R.string.pref_daily_driving_hours_default));
        try {
            preferenceDrivingHours = Integer.parseInt(preferences.getString(getString(R.string.pref_key_daily_driving_hours), getString(R.string.pref_daily_driving_hours_default)));
        } catch (NumberFormatException nfe) {
            preferenceDrivingHours = defaultHours;
        }
    }


    @Override
    public void onTripSaveRequest() {
        TripManager tripManager = new TripManager();
        Trip trip = tripManager.buildTrip(tripViewModel, firebaseUser.getUid());
        List<TripDay> tripDays = tripManager.buildInitialTripDays(this, tripViewModel, preferenceDrivingHours);
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

    private void onSignedInInitialize(FirebaseUser firebaseUser) {
        this.activeUsername = firebaseUser.getDisplayName();
        String userId = firebaseUser.getUid();
        this.saveUserPreference(userId);
        new UserInfoSave().execute(firebaseUser);
        this.tripArchiveCheck(userId);
    }

    private void tripArchiveCheck(String userId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String now = PersistenceFormats.toDateString(LocalDate.now());
        String lastArchiveCheck = preferences.getString(ArgumentKeys.LAST_TRIP_ARCHIVE_CHECK_DATE, now);
        LocalDate lastArchiveCheckDate = LocalDate.parse(lastArchiveCheck);
        // run TripArchiver task if last archive check was prior to current date
        if (lastArchiveCheckDate.compareTo(LocalDate.now()) < 0) {
            new TripArchiver().execute(userId);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(ArgumentKeys.LAST_TRIP_ARCHIVE_CHECK_DATE, now);
            editor.apply();
        }
    }

    private void saveUserPreference(String userId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ArgumentKeys.APPLICATION_USER_ID, userId);
        editor.apply();
    }

    private void onSignedOutCleanup() {
        this.activeUsername = ANONYMOUS;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(FragmentTags.KEY_ACTIVE_FRAGMENT_TAG, activeFragmentTag);
        super.onSaveInstanceState(savedInstanceState);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (activeFragmentTag.equals(FragmentTags.TAG_TRIP_DETAIL)) {
            if (menu.findItem(MenuCodes.ID_ARCHIVE_TRIP) == null) {
                menu.add(0, MenuCodes.ID_ARCHIVE_TRIP, 2, getString(R.string.menu_label_archive_trip));
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void tripDisplayCommunication(String tripNodeKey) {
        activeDisplayedTripNodeKey = tripNodeKey;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
            case MenuCodes.ID_ARCHIVE_TRIP:
                TripRepository repository = new TripRepository();
                repository.archiveTrip(userId, activeDisplayedTripNodeKey);
                onFragmentNavigationRequest(FragmentTags.TAG_TRIP_LIST);
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
        Bundle args = null;

        // TODO: support all fragments here
        switch (item.getItemId()) {
            case R.id.nav_trip_plans:
                fragmentTag = FragmentTags.TAG_TRIP_LIST;
                break;
            case R.id.nav_create_trip:
                fragmentTag = FragmentTags.TAG_CREATE_TRIP;
                break;
            case R.id.nav_trip_history:
                args = new Bundle();
                args.putString(ArgumentKeys.TRIP_LIST_DISPLAY_ARCHIVE_INDICATOR, String.valueOf(true));
                fragmentTag = FragmentTags.TAG_TRIP_LIST;
                break;
            case R.id.nav_settings:
                fragmentTag = FragmentTags.TAG_SETTINGS_PREFERENCES;
                break;
            default:
                fragmentTag = FragmentTags.TAG_TRIP_LIST;
                break;
        }

        fragment = getFragmentInstance(fragmentTag, true);
        if (args != null) {
            fragment.setArguments(args);
        }
        loadFragment(fragment, fragmentTag, true);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Fragment getFragmentInstance(String fragmentTag, boolean forceNavigation) {
        Class fragmentClass;
        Fragment result = null;

        // if not forcing a navigation event, check if Fragment has already been initialized, such as following configuration change
        if (!forceNavigation) {
            result = getSupportFragmentManager().findFragmentByTag(fragmentTag);
            if (result != null) {
                return result;
            }
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
            case FragmentTags.TAG_TRIP_DAY:
                fragmentClass = TripDayFragment.class;
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

    // forceNavigation param of TRUE is intended to force a load even if Fragment exists, for basic navigation events.
    // Basic navigation was the original purpose of this method. In cases of device configuration change (rotation in particular)
    // we pass forceNavigation false and expect findFragmentByTag() will NOT be null and therefore
    // Fragment will not be explicitly loaded, allowing the system to restore it with view hierarchy.
    private void loadFragment(Fragment fragment, String fragmentTag, boolean forceNavigation) {
        InputUtils.hideKeyboard(this);
        if (!NetworkChecker.isNetworkConnected(this)) {
            Snackbar.make(contentFrameLayout, R.string.warning_message_no_network, Snackbar.LENGTH_LONG).show();
        }
        if (forceNavigation || getSupportFragmentManager().findFragmentByTag(fragmentTag) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, fragment, fragmentTag)
                    .addToBackStack(null)
                    .commit();
            activeFragmentTag = fragmentTag;
        }
    }

    @Override
    public void onMapDisplayRequested(int requestCode, String returnToFragmentTag) {
        if (lastKnownLocation == null) {
            Snackbar.make(contentFrameLayout, R.string.error_device_location_null, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!NetworkChecker.isNetworkConnected(this)) {
            Snackbar.make(contentFrameLayout, R.string.error_message_no_network_no_maps, Snackbar.LENGTH_LONG).show();
            return;
        }
        Fragment fragment = GoogleMapFragment.newInstance(requestCode, returnToFragmentTag);
        loadFragment(fragment, FragmentTags.TAG_MAP_SELECT_LOCATION, true);
    }

    @Override
    public void onMapDisplayRequested(int requestCode, String returnToFragmentTag, LatLng displayLocation, String locationDescription) {
        if (lastKnownLocation == null) {
            Snackbar.make(contentFrameLayout, R.string.error_device_location_null, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!NetworkChecker.isNetworkConnected(this)) {
            Snackbar.make(contentFrameLayout, R.string.error_message_no_network_no_maps, Snackbar.LENGTH_LONG).show();
            return;
        }
        Bundle args = new Bundle();
        args.putFloat(MapSettings.KEY_MAP_DISPLAY_LATITUDE, (float)displayLocation.latitude);
        args.putFloat(MapSettings.KEY_MAP_DISPLAY_LONGITUDE, (float)displayLocation.longitude);
        if (locationDescription != null) {
            args.putString(MapSettings.KEY_MAP_DISPLAY_LOCATION_DESCRIPTION, locationDescription);
        }
        Fragment fragment = GoogleMapFragment.newInstance(requestCode, returnToFragmentTag);
        if (fragment.getArguments() != null) {
            fragment.getArguments().putAll(args);
        } else {
            fragment.setArguments(args);
        }
        loadFragment(fragment, FragmentTags.TAG_MAP_SELECT_LOCATION, true);
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

    @Override
    public void onTripDayDestinationSelected(LatLng location, int requestCode, String locationDescription) {
        if (requestCode == RequestCodes.TRIP_DAY_DESTINATION_REQUEST_CODE) {
            tripDayViewModel.getDestinations().add(new TripLocationViewModel(location.latitude, location.longitude, locationDescription, null));
            TripRepository repository = new TripRepository();
            repository.updateTripDay(
                    firebaseUser.getUid(),
                    tripDayViewModel.getTripId(),
                    tripDayViewModel.getTripDayNodeKey(),
                    tripDayViewModel.asTripDay()
            );
        }
    }

    private void verifyPermissions() {
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
                        Log.i(TAG, "lastKnownLocation updated");
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
        Fragment fragment = getFragmentInstance(fragmentTag, true);
        loadFragment(fragment, fragmentTag, true);
    }

    @Override
    public void onFragmentNavigationRequest(String fragmentTag, String tripId, String tripNodeKey, boolean isArchived) {
        Fragment fragment = getFragmentInstance(fragmentTag, true);
        Bundle args = new Bundle();
        args.putString(ArgumentKeys.KEY_TRIP_ID, tripId);
        args.putString(ArgumentKeys.KEY_TRIP_NODE_KEY, tripNodeKey);
        args.putBoolean(ArgumentKeys.TRIP_IS_ARCHIVED_KEY, isArchived);
        fragment.setArguments(args);
        loadFragment(fragment, fragmentTag, true);
    }

    @Override
    public void onTripDayEditFragmentRequest(String fragmentTag, String tripId, String tripNodeKey, int dayNumber, String nodeKey) {
        Fragment fragment = getFragmentInstance(fragmentTag, true);
        Bundle args = new Bundle();
        args.putString(ArgumentKeys.KEY_TRIP_ID, tripId);
        args.putString(ArgumentKeys.KEY_TRIP_NODE_KEY, tripNodeKey);
        args.putInt(ArgumentKeys.KEY_TRIP_DAY_NUMBER, dayNumber);
        args.putString(ArgumentKeys.KEY_DAY_NODE_KEY, nodeKey);
        fragment.setArguments(args);
        loadFragment(fragment, fragmentTag, true);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // currently only the driving hours preference needs to be handled when changed
        if (key.equals(getString(R.string.pref_key_daily_driving_hours))) {
            loadPreferences();
        }
    }

    private class FragmentLifecycleListener extends FragmentManager.FragmentLifecycleCallbacks {
        // Required to maintain activeFragmentTag when device back key is pressed.
        // Without this, pressing device back key, followed by device rotation which triggered lifecycle events, would load incorrect fragment.
        @Override
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            String tag = f.getTag();
            if (tag != null) {
                activeFragmentTag = tag;
            }
            super.onFragmentViewCreated(fm, f, v, savedInstanceState);
        }
    }

}
