package ericrybarczyk.me.roadtrippy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    CreateTripFragment.OnFragmentInteractionListener,
                    TripListFragment.OnFragmentInteractionListener,
                    CreateTripFragment.MapDisplayRequestListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_ACTIVE_FRAGMENT_TAG = "active_fragment_tag";
    private static final String FRAG_TAG_CREATE_TRIP = "create_trip_fragment";
    private static final String FRAG_TAG_TRIP_LIST = "trip_list_fragment";
    private static final String FRAG_TAG_MAP_SELECT_LOCATION = "google_map_select_location_fragment";
    public static final int RC_SIGN_IN = 1;

    public static final String ANONYMOUS = "anonymous";
    private String activeUsername = ANONYMOUS;

    private String activeFragmentTag;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @BindView(R.id.toolbar) protected Toolbar toolbar;
    @BindView(R.id.fab) protected FloatingActionButton fab;
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
                    TextView usernameText = header.findViewById(R.id.username_display_text);
                    usernameText.setText(user.getDisplayName());

                    onSignedInInitialize(user.getDisplayName());

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
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
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
        activeFragmentTag = savedInstanceState.getString(KEY_ACTIVE_FRAGMENT_TAG, FRAG_TAG_TRIP_LIST);
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
        Fragment fragment = getFragmentInstance(FRAG_TAG_CREATE_TRIP);
        loadFragment(fragment, FRAG_TAG_CREATE_TRIP);
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
                fragmentTag = FRAG_TAG_TRIP_LIST;
                break;
            case R.id.nav_create_trip:
                fragmentTag = FRAG_TAG_CREATE_TRIP;
                break;
            case R.id.nav_trip_ideas:

                break;
            case R.id.nav_trip_history:

                break;
            case R.id.nav_settings:

                break;
            default:
                fragmentTag = FRAG_TAG_TRIP_LIST;
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
            case FRAG_TAG_TRIP_LIST:
                fragmentClass = TripListFragment.class;
                break;
            case FRAG_TAG_CREATE_TRIP:
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

        if (fragmentTag.equals(FRAG_TAG_CREATE_TRIP)) {
            ((CreateTripFragment) result).setMapDisplayRequestListener(this);
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
        if (fragmentTag.equals(FRAG_TAG_CREATE_TRIP) || fragmentTag.equals(FRAG_TAG_MAP_SELECT_LOCATION)) {
            fab.setVisibility(View.INVISIBLE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i(TAG, "onFragmentInteraction for Uri: " + uri.toString());
    }

    @Override
    public void onMapDisplayRequested() {
        Fragment fragment = GoogleMapFragment.newInstance();
        loadFragment(fragment, FRAG_TAG_MAP_SELECT_LOCATION);
    }
}
