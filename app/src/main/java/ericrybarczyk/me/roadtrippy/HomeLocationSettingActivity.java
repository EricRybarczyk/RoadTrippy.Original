package ericrybarczyk.me.roadtrippy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import ericrybarczyk.me.roadtrippy.util.FragmentTags;
import ericrybarczyk.me.roadtrippy.util.MapSettings;
import ericrybarczyk.me.roadtrippy.util.RequestCodes;

public class HomeLocationSettingActivity extends AppCompatActivity
                                        implements FragmentNavigationRequestListener, GoogleMapFragment.LocationSelectedListener {

    private static final String TAG = HomeLocationSettingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_location_setting);

        Fragment fragment = GoogleMapFragment.newInstance(RequestCodes.PREFERENCE_HOME_LOCATION_REQUEST_CODE, FragmentTags.TAG_SETTINGS_PREFERENCES);

        // center the map on existing home location preference if exists
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (preferences.contains(getString(R.string.pref_key_home_latitude))) {
            Bundle args = new Bundle();
            float existingHomeLatitude = preferences.getFloat(getString(R.string.pref_key_home_latitude), 0);
            float existingHomeLongitude = preferences.getFloat(getString(R.string.pref_key_home_longitude), 0);
            if (existingHomeLatitude != 0 && existingHomeLongitude != 0) {
                args.putFloat(MapSettings.KEY_MAP_DISPLAY_LATITUDE, existingHomeLatitude);
                args.putFloat(MapSettings.KEY_MAP_DISPLAY_LONGITUDE, existingHomeLongitude);
                if (fragment.getArguments() != null) {
                    fragment.getArguments().putAll(args);
                } else {
                    fragment.setArguments(args);
                }
            }
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, fragment, FragmentTags.TAG_HOME_LOCATION_PREFERENCE)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onFragmentNavigationRequest(String fragmentTag) {
        Class destination = MainActivity.class;
        Intent intent = new Intent(this, destination);
        startActivity(intent);
    }

    @Override
    public void onFragmentNavigationRequest(String fragmentTag, String tripId, String tripDescription, boolean isArchived) {
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
