package ericrybarczyk.me.roadtrippy;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public final class RoadTrippyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}
