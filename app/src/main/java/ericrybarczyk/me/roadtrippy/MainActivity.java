package ericrybarczyk.me.roadtrippy;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    CreateTripFragment.OnFragmentInteractionListener,
                    TripListFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_ACTIVE_FRAGMENT_TAG = "active_fragment_tag";
    private String activeFragmentTag;
    private static final String FRAG_TAG_CREATE_TRIP = "create_trip_fragment";
    private static final String FRAG_TAG_TRIP_LIST = "trip_list_fragment";

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        return result;
    }

    private void loadFragment(Fragment fragment, String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, fragment, fragmentTag)
                .addToBackStack(null)
                .commit();
        activeFragmentTag = fragmentTag;
        if (fragmentTag.equals(FRAG_TAG_CREATE_TRIP)) {
            fab.setVisibility(View.INVISIBLE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i(TAG, "onFragmentInteraction for Uri: " + uri.toString());
    }
}
