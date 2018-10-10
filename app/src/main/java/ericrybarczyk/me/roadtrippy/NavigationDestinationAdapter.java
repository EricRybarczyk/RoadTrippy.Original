package ericrybarczyk.me.roadtrippy;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.viewmodels.TripLocationViewModel;

public class NavigationDestinationAdapter { //extends RecyclerView.Adapter<NavigationDestinationAdapter.NavigationDestinationHolder> {

    private List<TripLocationViewModel> destinations;

//    @NonNull
//    @Override
//    public NavigationDestinationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View view = inflater.inflate(R.layout.navigation_destination_list_item, parent, false);
//        return new NavigationDestinationHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull NavigationDestinationHolder holder, int position) {
//        TripLocationViewModel viewModel = destinations.get(position);
//
//    }
//
//    @Override
//    public int getItemCount() {
//        if (destinations == null) {
//            return 0;
//        }
//        return destinations.size();
//    }




}
