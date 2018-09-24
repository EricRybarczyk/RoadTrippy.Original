package ericrybarczyk.me.roadtrippy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ericrybarczyk.me.roadtrippy.util.FragmentTags;


public class TripListFragment extends Fragment {

    private FragmentNavigationRequestListener fragmentNavigationRequestListener;

    @BindView(R.id.fab) protected FloatingActionButton fab;

    public TripListFragment() {
        // Required empty public constructor
    }

    public static TripListFragment newInstance(String param1, String param2) {
        TripListFragment fragment = new TripListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        fragmentNavigationRequestListener.onFragmentNavigationRequest(FragmentTags.TAG_CREATE_TRIP);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigationRequestListener) {
            fragmentNavigationRequestListener = (FragmentNavigationRequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentNavigationRequestListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentNavigationRequestListener = null;
    }

}
