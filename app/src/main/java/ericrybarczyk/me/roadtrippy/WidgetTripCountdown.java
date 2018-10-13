package ericrybarczyk.me.roadtrippy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ericrybarczyk.me.roadtrippy.dto.Trip;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;
import ericrybarczyk.me.roadtrippy.util.ArgumentKeys;
import ericrybarczyk.me.roadtrippy.util.RequestCodes;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;

public class WidgetTripCountdown extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String userId = WidgetTripCountdownConfigActivity.getUserIdPref(context, appWidgetId);
        String tripId = WidgetTripCountdownConfigActivity.getTripIdPref(context, appWidgetId);
        String tripNodeKey = WidgetTripCountdownConfigActivity.getTripNodeKeyPref(context, appWidgetId);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_trip_countdown);

        // get trip data from firebase
        TripRepository tripRepository = new TripRepository();

        DatabaseReference reference = tripRepository.getTrip(userId, tripNodeKey);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Trip trip  = dataSnapshot.getValue(Trip.class);
                if (trip == null) {
                    Log.e(WidgetTripCountdown.class.getSimpleName(), "updateAppWidget - onDataChange: Trip object is null from Firebase");
                    return;
                }
                TripViewModel viewModel = TripViewModel.from(trip);
                // TODO: idea: show a Star (fontawesome) if daysToGo is zero - also adjust subtitle text
                String daysToGo = String.valueOf(viewModel.getDaysUntilDeparture());
                String subtitle = daysToGo + " " + context.getString(R.string.widget_trip_countdown_subtitle);
                remoteViews.setTextViewText(R.id.widget_trip_countdown_days_to_go, daysToGo);
                remoteViews.setTextViewText(R.id.widget_trip_countdown_title, viewModel.getDescription());
                remoteViews.setTextViewText(R.id.widget_trip_countdown_subtitle, subtitle);

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(ArgumentKeys.WIDGET_REQUEST_TRIP_ID, tripId);
                intent.putExtra(ArgumentKeys.WIDGET_REQUEST_TRIP_NODE_KEY, tripNodeKey);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, RequestCodes.WIDGET_TRIP_COUNTDOWN_TRIP_CLICK, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.widget_trip_countdown_layout_container, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(WidgetTripCountdown.class.getSimpleName(), databaseError.getMessage());
            }
        });

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There can be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WidgetTripCountdownConfigActivity.deleteWidgetPref(context, appWidgetId);
        }
    }
}
