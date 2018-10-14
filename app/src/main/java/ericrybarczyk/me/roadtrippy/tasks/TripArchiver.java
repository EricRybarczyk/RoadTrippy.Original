package ericrybarczyk.me.roadtrippy.tasks;

import android.os.AsyncTask;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;

public class TripArchiver extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        TripRepository repository = new TripRepository();
        String userId = strings[0];
        repository.archiveFinishedTrips(userId);
        return null;
    }
}
