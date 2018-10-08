package ericrybarczyk.me.roadtrippy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.util.RequestCodes;

public class HomeLocationSettingActivity extends AppCompatActivity
                                        implements FragmentNavigationRequestListener, GoogleMapFragment.LocationSelectedListener {

    private static final String TAG = HomeLocationSettingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_location_setting);


        Fragment fragment = GoogleMapFragment.newInstance(RequestCodes.PREFERENCE_HOME_LOCATION_REQUEST_CODE, FragmentTags.TAG_SETTINGS_PREFERENCES);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, fragment, FragmentTags.TAG_HOME_LOCATION_PREFERENCE)
                .addToBackStack(null)
                .commit();


    }

    @Override
    public void onFragmentNavigationRequest(String fragmentTag) {
        // always go back to the main Settings screen
        Class fragmentClass = SettingsFragment.class;
        Fragment result = null;
        try {
            result = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            Log.e(TAG, "Unable to instantiate instance of " + fragmentClass.getSimpleName() + " : " + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Illegal Access on instance of " + fragmentClass.getSimpleName() + " : " + e.getMessage());
        }
        if (result != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, result, fragmentTag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onFragmentNavigationRequest(String fragmentTag, String tripId, String tripDescription) {
        // this overload is not called from GoogleMapFragment so it is not implemented
        throw new UnsupportedOperationException(TAG + ": onFragmentNavigationRequest is not implemented in this Activity");
    }

    @Override
    public void onTripDayEditFragmentRequest(String fragmentTag, String tripId, String tripNodeKey, int dayNumber, String nodeKey) {
        // this overload is not called from GoogleMapFragment so it is not implemented
        throw new UnsupportedOperationException(TAG + ": onTripDayEditFragmentRequest is not implemented in this Activity");
    }

    @Override
    public void onLocationSelected(LatLng location, int requestCode, String locationDescription) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(getString(R.string.pref_key_home_latitude), (float)location.latitude);
        editor.putFloat(getString(R.string.pref_key_home_longitude), (float)location.longitude);
        editor.apply();
    }

    @Override
    public void onTripDayDestinationSelected(LatLng location, int requestCode, String locationDescription) {
        // this interface method is not called from the Google map fragment when setting Home location preference so it is not implemented
        throw new UnsupportedOperationException(TAG + ": onTripDayDestinationSelected is not implemented in this Activity");
    }
}
