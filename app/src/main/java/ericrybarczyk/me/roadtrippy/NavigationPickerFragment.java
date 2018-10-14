package ericrybarczyk.me.roadtrippy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import ericrybarczyk.me.roadtrippy.dto.TripLocation;
import ericrybarczyk.me.roadtrippy.endpoints.NavigationIntentService;
import ericrybarczyk.me.roadtrippy.persistence.TripRepository;
import ericrybarczyk.me.roadtrippy.util.ArgumentKeys;
import ericrybarczyk.me.roadtrippy.viewmodels.TripLocationViewModel;

public class NavigationPickerFragment extends DialogFragment {

    private String dayNodeKey;
    private String userId;
    private String tripId;
    TripRepository tripRepository;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @BindView(R.id.navigation_destination_list) protected RecyclerView navigationDestinationsRecyclerView;

    public NavigationPickerFragment() {
    }

    public static NavigationPickerFragment newInstance(String tripId, String dayNodeKey) {
        NavigationPickerFragment navFragment = new NavigationPickerFragment();
        Bundle args = new Bundle();
        args.putString(ArgumentKeys.KEY_TRIP_ID, tripId);
        args.putString(ArgumentKeys.KEY_DAY_NODE_KEY, dayNodeKey);
        navFragment.setArguments(args);
        navFragment.setStyle(STYLE_NO_TITLE, 0);
        return navFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            dayNodeKey = savedInstanceState.getString(ArgumentKeys.KEY_DAY_NODE_KEY);
            tripId = savedInstanceState.getString(ArgumentKeys.KEY_TRIP_ID);
        } else if (getArguments() != null) {
            dayNodeKey = getArguments().getString(ArgumentKeys.KEY_DAY_NODE_KEY);
            tripId = getArguments().getString(ArgumentKeys.KEY_TRIP_ID);
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        tripRepository = new TripRepository();
        DatabaseReference reference = tripRepository.getDestinationsForTripDay(userId, tripId, dayNodeKey);

        FirebaseRecyclerOptions<TripLocation> options = new FirebaseRecyclerOptions.Builder<TripLocation>()
                .setQuery(reference, TripLocation.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TripLocation, NavigationDestinationHolder>(options) {

            @NonNull
            @Override
            public NavigationDestinationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_destination_list_item, parent, false);
                return new NavigationDestinationHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull NavigationDestinationHolder holder, int position, @NonNull TripLocation model) {
                TripLocationViewModel viewModel = TripLocationViewModel.from(model);
                holder.navigateButton.setText(viewModel.getDescription());
//                holder.setDestinationLatitude(viewModel.getLatitude());
//                holder.setDestinationLongitude(viewModel.getLongitude());

                // TODO: click listener for Navigate function
                holder.navigateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent navigationIntent = NavigationIntentService.getNavigationIntent(viewModel);
                        if (navigationIntent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivity(navigationIntent);
                        } else {
                            Toast.makeText(getContext(), R.string.error_message_system_missing_google_maps, Toast.LENGTH_LONG).show();
                        }
                        dismiss(); // TODO - is this the right call here?
                    }
                });
            }
        };

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.dialog_select_navigation_destination, container);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        navigationDestinationsRecyclerView.setLayoutManager(layoutManager);
        navigationDestinationsRecyclerView.setAdapter(firebaseRecyclerAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ArgumentKeys.KEY_TRIP_ID, tripId);
        outState.putString(ArgumentKeys.KEY_DAY_NODE_KEY, dayNodeKey);
        super.onSaveInstanceState(outState);
    }
}
