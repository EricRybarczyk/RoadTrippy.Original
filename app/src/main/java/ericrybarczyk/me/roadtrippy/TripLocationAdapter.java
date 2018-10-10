package ericrybarczyk.me.roadtrippy;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;
import ericrybarczyk.me.roadtrippy.util.FontManager;
import ericrybarczyk.me.roadtrippy.viewmodels.TripLocationViewModel;

public class TripLocationAdapter extends RecyclerView.Adapter<TripLocationAdapter.TripLocationHolder> {

    private List<TripLocationViewModel> destinations;
    private String userId;
    private String tripId;
    private String dayNodeKey;

    public TripLocationAdapter(List<TripLocationViewModel> tripLocationViewModelList, String userId, String tripId, String dayNodeKey) {
        this.destinations = tripLocationViewModelList;
        this.userId = userId;
        this.tripId = tripId;
        this.dayNodeKey = dayNodeKey;
    }


    @NonNull
    @Override
    public TripLocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trip_day_destination_list_item, parent, false);
        return new TripLocationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripLocationHolder holder, int position) {
        TripLocationViewModel viewModel = destinations.get(position);

        holder.destinationDescription.setText(viewModel.getDescription());

        holder.iconKeep.setTypeface(FontManager.getTypeface(holder.iconKeep.getContext(), FontManager.FONTAWESOME_REGULAR));
        holder.iconKeep.setTextColor(ContextCompat.getColor(holder.iconKeep.getContext(), R.color.colorControlHighlightSafe));
        holder.iconKeep.setVisibility(View.INVISIBLE);

        holder.iconTrash.setTypeface(FontManager.getTypeface(holder.iconTrash.getContext(), FontManager.FONTAWESOME_REGULAR));
        holder.iconTrash.setTextColor(ContextCompat.getColor(holder.iconTrash.getContext(), R.color.colorControlHighlightOff));
        holder.iconTrash.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        if (destinations == null) {
            return 0;
        }
        return destinations.size();
    }


    public class TripLocationHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.destination_description) protected TextView destinationDescription;
        @BindView(R.id.icon_keep) protected TextView iconKeep;
        @BindView(R.id.icon_trash) protected TextView iconTrash;

        private boolean trashIconActivated;

        public TripLocationHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.icon_trash)
        public void onClickTrash() {
            trashIconActivated = !trashIconActivated; // flip the toggle
            if (trashIconActivated) {
                iconTrash.setTextColor(ContextCompat.getColor(iconTrash.getContext(), R.color.colorControlHighlightWarning));
                iconKeep.setVisibility(View.VISIBLE);
            } else {
                // not-activated means second click so we delete the item and then reset state of the icons
                int index = this.getAdapterPosition();
                TripRepository repository = new TripRepository();
                repository.removeTripDayDestination(userId, tripId, dayNodeKey, index);
                destinations.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, getItemCount());
                iconTrash.setTextColor(ContextCompat.getColor(iconTrash.getContext(), R.color.colorControlHighlightOff));
                iconKeep.setVisibility(View.INVISIBLE);
            }
        }

        @OnClick(R.id.icon_keep)
        public void onClickKeep() {
            iconKeep.setVisibility(View.INVISIBLE);
            trashIconActivated = false;
            iconTrash.setTextColor(ContextCompat.getColor(iconTrash.getContext(), R.color.colorControlHighlightOff));
        }
    }

}
