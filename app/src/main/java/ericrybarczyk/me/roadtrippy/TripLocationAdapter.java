package ericrybarczyk.me.roadtrippy;

import android.content.Context;
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
import ericrybarczyk.me.roadtrippy.util.FontManager;
import ericrybarczyk.me.roadtrippy.viewmodels.TripLocationViewModel;

public class TripLocationAdapter extends RecyclerView.Adapter<TripLocationAdapter.TripLocationHolder> {

    private List<TripLocationViewModel> locations;
    private final Context parentContext;

    public TripLocationAdapter(Context parentContext, List<TripLocationViewModel> tripLocationViewModelList) {
        this.parentContext = parentContext;
        this.locations = tripLocationViewModelList;
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
        TripLocationViewModel viewModel = locations.get(position);

        holder.destinationDescription.setText(viewModel.getDescription());

        Context c = parentContext;

        holder.iconKeep.setTypeface(FontManager.getTypeface(holder.iconKeep.getContext(), FontManager.FONTAWESOME_REGULAR));
        holder.iconKeep.setTextColor(ContextCompat.getColor(holder.iconKeep.getContext(), R.color.colorControlHighlightSafe));
        holder.iconKeep.setVisibility(View.INVISIBLE);

        holder.iconTrash.setTypeface(FontManager.getTypeface(holder.iconTrash.getContext(), FontManager.FONTAWESOME_REGULAR));
        holder.iconTrash.setTextColor(ContextCompat.getColor(holder.iconTrash.getContext(), R.color.colorControlHighlightOff));
        holder.iconTrash.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        if (locations == null) {
            return 0;
        }
        return locations.size();
    }


    public class TripLocationHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.destination_description)
        protected TextView destinationDescription;
        @BindView(R.id.icon_keep)
        protected TextView iconKeep;
        @BindView(R.id.icon_trash)
        protected TextView iconTrash;

        private boolean trashIconActivated;

        public TripLocationHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.icon_trash)
        public void onClickTrash() {
            trashIconActivated = !trashIconActivated;
            if (trashIconActivated) {
                iconTrash.setTextColor(ContextCompat.getColor(iconTrash.getContext(), R.color.colorControlHighlightWarning));
                iconKeep.setVisibility(View.VISIBLE);
            } else {
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
