package ericrybarczyk.me.roadtrippy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.viewmodels.TripViewModel;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripHolder> {

    private List<TripViewModel> tripList;
    private final Context parentContext;
    private final TripAdapterOnClickHandler onTripClickHandler;


    public interface TripAdapterOnClickHandler {
        void onClick(String tripId);
    }

    TripAdapter(Context context, TripAdapterOnClickHandler tripAdapterOnClickHandler) {
        parentContext = context;
        onTripClickHandler = tripAdapterOnClickHandler;
        tripList = new ArrayList<>();
    }

    public void setTripList(List<TripViewModel> trips) { // TODO: probably get rid of this?
        tripList = trips;
        notifyDataSetChanged();
    }

    public void addTrip(TripViewModel tripViewModel) {
        tripList.add(tripViewModel);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trip_list_item, parent, false);
        return new TripHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripHolder holder, int position) {
        TripViewModel viewModel = tripList.get(position);

        String dateRangeText = viewModel.getDepartureDate().getMonth()
                                        .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                + " " + String.valueOf(viewModel.getDepartureDate().getDayOfMonth()); // TODO: finish building the display string, or add to view model itself!! And use string builder.

        holder.tripName.setText(viewModel.getDescription());
        holder.tripDateRange.setText(dateRangeText);
    }

    @Override
    public int getItemCount() {
        if (tripList == null) {
            return 0;
        }
        return tripList.size();
    }


    public class TripHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.trip_name_text) protected TextView tripName;
        @BindView(R.id.trip_directions_overview_text) protected TextView tripDirectionsOverview;
        @BindView(R.id.trip_date_range_text) protected TextView tripDateRange;

        public TripHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String tripId = tripList.get(adapterPosition).getTripId();
            onTripClickHandler.onClick(tripId);
        }
    }
}
