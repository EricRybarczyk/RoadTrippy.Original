package ericrybarczyk.me.roadtrippy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.util.RequestCodes;


public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    CreateTripFragment.MapDisplayRequestListener,
                    FragmentNavigationRequestListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_ACTIVE_FRAGMENT_TAG = "active_fragment_tag";

    public static final String ANONYMOUS = "anonymous";
    private String activeUsername = ANONYMOUS;

    private String activeFragmentTag;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

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

        // initialize Firebase components
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        // SOURCE: FirebaseAuth code is directly adapted from Udacity & Google materials,
        // including the Firebase extracurricular module in the Android Developer Nanodegree program,
        // and https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // TODO: look at this https://stackoverflow.com/questions/32806735/refresh-header-in-navigation-drawer/35952939#35952939 and consider different spot to set this username textview
                    View header = navigationView.getHeaderView(0);
                    activeUsername = user.getDisplayName();
                    TextView usernameText = header.findViewById(R.id.username_display_text);
                    usernameText.setText(activeUsername);
                    onSignedInInitialize(activeUsername);

                } else {

                    onSignedOutCleanup();

                    // configure supported signin providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.EmailBuilder().build());

                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false) // TODO: set true. Udacity tutorial set this to false (default is true: system will basically keep user automatically logged in)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RequestCodes.SIGN_IN_REQUEST_CODE);
                }
            }
        };
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
        // TODO: attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        this.activeUsername = ANONYMOUS;
        // TODO: someDataAdapter.clear() and detachDatabaseReadListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(KEY_ACTIVE_FRAGMENT_TAG, activeFragmentTag);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState); // call super first to restore view hierarchy
        activeFragmentTag = savedInstanceState.getString(KEY_ACTIVE_FRAGMENT_TAG, FragmentTags.FRAG_TAG_TRIP_LIST);
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
        // TODO: someDataAdapter.clear() and detachDatabaseReadListener();
    }

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        Fragment fragment = getFragmentInstance(FragmentTags.FRAG_TAG_CREATE_TRIP);
        loadFragment(fragment, FragmentTags.FRAG_TAG_CREATE_TRIP);
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
            case R.id.action_sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
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
                fragmentTag = FragmentTags.FRAG_TAG_TRIP_LIST;
                break;
            case R.id.nav_create_trip:
                fragmentTag = FragmentTags.FRAG_TAG_CREATE_TRIP;
                break;
            case R.id.nav_trip_ideas:

                break;
            case R.id.nav_trip_history:

                break;
            case R.id.nav_settings:

                break;
            default:
                fragmentTag = FragmentTags.FRAG_TAG_TRIP_LIST;
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
            case FragmentTags.FRAG_TAG_TRIP_LIST:
                fragmentClass = TripListFragment.class;
                break;
            case FragmentTags.FRAG_TAG_CREATE_TRIP:
                fragmentClass = CreateTripFragment.class;
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
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, fragment, fragmentTag)
                .addToBackStack(null)
                .commit();
        activeFragmentTag = fragmentTag;
    }

    @Override
    public void onMapDisplayRequested(GoogleMapFragment.LocationSelectedListener callbackListener, int requestCode, String returnToFragmentTag) {
        Fragment fragment = GoogleMapFragment.newInstance(lastKnownLocation, callbackListener, requestCode, returnToFragmentTag);
        loadFragment(fragment, FragmentTags.FRAG_TAG_MAP_SELECT_LOCATION);
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
}
