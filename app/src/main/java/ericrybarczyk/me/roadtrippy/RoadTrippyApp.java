package ericrybarczyk.me.roadtrippy;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;

public final class RoadTrippyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);

        // call setPersistenceEnabled() in Application class: https://stackoverflow.com/a/37766261/798642
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
