package ericrybarczyk.me.roadtrippy.tasks;

import android.os.AsyncTask;
import com.google.firebase.auth.FirebaseUser;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;

public class UserInfoSave extends AsyncTask<FirebaseUser, Void, Void> {

    @Override
    protected Void doInBackground(FirebaseUser... firebaseUsers) {
        TripRepository repository = new TripRepository();
        repository.saveUserInfo(firebaseUsers[0]);
        return null;
    }
}
