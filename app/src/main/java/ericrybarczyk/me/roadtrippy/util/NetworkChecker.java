package ericrybarczyk.me.roadtrippy.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import ericrybarczyk.me.roadtrippy.R;

public class NetworkChecker {

    /*  This method was directly adapted from https://stackoverflow.com/a/4009133/798642
        as recommended by Udacity for the PopularMovies app project.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // I prefer the affirmative (is) method name prefix
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null) && (networkInfo.isConnected());
    }

}
