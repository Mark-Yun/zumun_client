package com.mark.zumo.client.customer.view.place;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.place.adapter.LatestVisitStoreAdapter;
import com.mark.zumo.client.customer.view.place.adapter.NearbyStoreAdapter;
import com.mark.zumo.client.customer.viewmodel.PlaceViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 18.
 */
public class PlaceFragment extends Fragment {

    @BindView(R.id.latest_visit_store) RecyclerView latestVisitStore;
    @BindView(R.id.near_by_store) RecyclerView nearByStore;
    private PlaceViewModel placeViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        placeViewModel = ViewModelProviders.of(this).get(PlaceViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);
        ButterKnife.bind(this, view);
        inflateLatestVisitRecyclerView();
        inflateNearbyStoreRecyclerView();
        return view;
    }

    private void inflateLatestVisitRecyclerView() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        latestVisitStore.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        latestVisitStore.setLayoutManager(layoutManager);

        LatestVisitStoreAdapter adapter = new LatestVisitStoreAdapter();
        latestVisitStore.setAdapter(adapter);

        placeViewModel.latestVisitStore().observe(this, adapter::setStoreList);
    }

    private void inflateNearbyStoreRecyclerView() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        nearByStore.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        nearByStore.setLayoutManager(layoutManager);

        NearbyStoreAdapter adapter = new NearbyStoreAdapter();
        nearByStore.setAdapter(adapter);

        placeViewModel.nearByStore().observe(this, adapter::setStoreList);
    }
}
