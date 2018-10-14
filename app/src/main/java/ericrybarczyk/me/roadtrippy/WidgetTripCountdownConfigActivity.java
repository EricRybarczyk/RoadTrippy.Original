package ericrybarczyk.me.roadtrippy;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;
import ericrybarczyk.me.roadtrippy.util.ArgumentKeys;
import ericrybarczyk.me.roadtrippy.util.NetworkChecker;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;

public class WidgetTripCountdownConfigActivity extends AppCompatActivity {

    public static final String ERROR_PREF_VALUE = "error_missing_required_pref";

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String PREFS_NAME = "me.ericrybarczyk.roadtrippy.widget.TripCountdown";
    private static final String PREF_KEY_TRIP_ID = "appwidget_trip_id_";
    private static final String PREF_KEY_TRIP_NODE_KEY = "appwidget_trip_node_key_";
    private static final String PREF_KEY_USER_ID = "appwidget_user_id_";
    private static final String TAG = WidgetTripCountdownConfigActivity.class.getSimpleName();

    TripRepository tripRepository;
    FirebaseListAdapter<Trip> adapter;
    String userId;
    @BindView(R.id.widget_trip_countdown_config_heading) protected TextView headingText;
    @BindView(R.id.widget_trip_countdown_config_trip_list) protected ListView tripList;
    @BindView(R.id.widget_trip_countdown_config_launch_app_button) protected Button launchAppButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED, null);

        setContentView(R.layout.widget_trip_countdown_configuration);
        ButterKnife.bind(this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
        } else {
            // check network
            if (!NetworkChecker.isNetworkConnected(this)) {
                Snackbar.make(tripList, R.string.warning_message_no_network, Snackbar.LENGTH_LONG).show();
            }
            // send them to the app if no userId
            if (userId == null) {
                launchAppButton.setVisibility(View.VISIBLE);
                launchAppButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Class destination = MainActivity.class;
                        Intent intent = new Intent(getApplicationContext(), destination);
                        startActivity(intent);
                    }
                });
                Snackbar.make(tripList, R.string.widget_config_require_login_message, Snackbar.LENGTH_INDEFINITE).show();
                return;
            }
        }

        // get the appWidgetId from the Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.e(TAG, "Intent Extras is missing required AppWidgetManager.EXTRA_APPWIDGET_ID");
            finish();
            return;
        }

        // get the data to show
        tripRepository = new TripRepository();
        DatabaseReference reference = tripRepository.getTripList(userId);

        FirebaseListOptions<Trip> options = new FirebaseListOptions.Builder<Trip>()
                .setQuery(reference, Trip.class)
                .setLayout(R.layout.widget_trip_countdown_config_trip_list_item)
                .build();

        adapter = new FirebaseListAdapter<Trip>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Trip model, int position) {
                TripViewModel viewModel = TripViewModel.from(model);

                TextView tripName = v.findViewById(R.id.widget_trip_countdown_config_trip_name_text);
                TextView tripDateRange = v.findViewById(R.id.widget_trip_countdown_config_trip_date_range_text);

                tripName.setText(viewModel.getDescription());
                tripDateRange.setText(viewModel.getDateRangeSummaryText(getString(R.string.word_for_TO)));

                String tripId = viewModel.getTripId();
                String tripNodeKey = this.getRef(position).getKey();

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Context context = WidgetTripCountdownConfigActivity.this;
                        // set the widget data from the selected item
                        savePrefs(context, appWidgetId, userId, tripId, tripNodeKey);

                        // initial update of the widget being created
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        WidgetTripCountdown.updateAppWidget(context, appWidgetManager, appWidgetId);

                        // create the Intent and finish the Activity
                        Intent widgetIntent = new Intent();
                        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        setResult(RESULT_OK, widgetIntent);
                        finish();
                    }
                });
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                super.onError(error);
                finish();
            }
        };
        tripList.setAdapter(adapter);
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void savePrefs(Context context, int appWidgetId, String userId, String tripId, String tripNodeKey) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_KEY_USER_ID + String.valueOf(appWidgetId), userId);
        prefs.putString(PREF_KEY_TRIP_ID + String.valueOf(appWidgetId), tripId);
        prefs.putString(PREF_KEY_TRIP_NODE_KEY + String.valueOf(appWidgetId), tripNodeKey);
        prefs.apply();
    }

    static String getUserIdPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_KEY_USER_ID + String.valueOf(appWidgetId), ERROR_PREF_VALUE);
    }

    static String getTripIdPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_KEY_TRIP_ID + String.valueOf(appWidgetId), ERROR_PREF_VALUE);
    }

    static String getTripNodeKeyPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_KEY_TRIP_NODE_KEY + String.valueOf(appWidgetId), ERROR_PREF_VALUE);
    }

    static void deleteWidgetPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_KEY_TRIP_ID + String.valueOf(appWidgetId));
        prefs.remove(PREF_KEY_TRIP_NODE_KEY + String.valueOf(appWidgetId));
        prefs.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
