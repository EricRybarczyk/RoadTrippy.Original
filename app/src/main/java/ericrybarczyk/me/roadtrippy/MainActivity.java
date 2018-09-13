package ericrybarczyk.me.roadtrippy;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        try {
            Fragment fragment = (CreateTripFragment.class).newInstance();
            swapFragment(fragment);
        } catch (InstantiationException e) {
            Log.e(TAG, "Unable to instantiate instance of CreateTripFragment : " + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Illegal Access on instance of CreateTripFragment : " + e.getMessage());
        }
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation clicks by loading the appropriate fragment
        Fragment fragment;
        Class fragmentClass = null;

        switch (item.getItemId()) {
            case R.id.nav_trip_plans:
                fragmentClass = TripListFragment.class;
                break;
            case R.id.nav_create_trip:
                fragmentClass = CreateTripFragment.class;
                break;
            case R.id.nav_trip_ideas:

                break;
            case R.id.nav_trip_history:

                break;
            case R.id.nav_settings:

                break;
        }
        // TODO: clean this up when the app is built out and there won't be nulls here
        if (fragmentClass == null) {
            Log.e(TAG, "Problem loading Fragment: fragmentClass is null");
            Toast.makeText(this, "Not Implemented Yet!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                swapFragment(fragment);
            } catch (InstantiationException e) {
                Log.e(TAG, "Unable to instantiate instance of " + fragmentClass.getSimpleName() + " : " + e.getMessage());
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Illegal Access on instance of " + fragmentClass.getSimpleName() + " : " + e.getMessage());
            }
        }
    
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void swapFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i(TAG, "onFragmentInteraction for Uri: " + uri.toString());
    }
}
