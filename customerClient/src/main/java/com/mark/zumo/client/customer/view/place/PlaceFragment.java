/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.place;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.SphericalUtil;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.place.adapter.NearbyStoreAdapter;
import com.mark.zumo.client.customer.viewmodel.PlaceViewModel;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 18.
 */
public class PlaceFragment extends Fragment {

    public static final String TAG = "PlaceFragment";
    private static final int REQUEST_CODE_PLACE_PICKER = 15;
    @BindView(R.id.near_by_store) RecyclerView nearByStore;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    private PlaceViewModel placeViewModel;
    private SupportMapFragment mapFragment;

    private NearbyStoreAdapter adapter;

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
        inflateSwipeRefreshLayout();
        inflateNearbyStoreRecyclerView();
        inflateMapFragment();
        return view;
    }

    private void inflateSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshNearByStore);
    }

    private void refreshNearByStore() {
        mapFragment.getMapAsync(this::onLocationLoaded);
    }

    @SuppressLint("MissingPermission")
    private void onReadyMap(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapClickListener(this::onClickMapFragment);
        googleMap.setOnMyLocationButtonClickListener(() -> onMyLocationButtonClickListener(googleMap));
        googleMap.setOnCameraIdleListener(() -> onCameraIdleListener(googleMap));

        placeViewModel.currentLocation().observe(this, latLng -> onLocationLoaded(googleMap, latLng));
    }

    private void onCameraIdleListener(GoogleMap googleMap) {
        LatLng target = googleMap.getCameraPosition().target;
        VisibleRegion visibleRegion = googleMap.getProjection().getVisibleRegion();
        double distance = SphericalUtil.computeDistanceBetween(visibleRegion.farLeft, target);
        placeViewModel.nearByStore(target, (int) distance).observe(this, this::onLoadNearByStoreList);
    }

    private boolean onMyLocationButtonClickListener(GoogleMap googleMap) {
        placeViewModel.currentLocation().observe(this, latLng -> onLocationLoaded(googleMap, latLng));
        return true;
    }

    private void inflateMapFragment() {
        mapFragment = new SupportMapFragment();
        FragmentManager fragmentManager = getFragmentManager();
        Objects.requireNonNull(fragmentManager).beginTransaction()
                .replace(R.id.map_fragment, mapFragment)
                .commit();

        mapFragment.getMapAsync(this::onReadyMap);
    }

    private void onLocationLoaded(GoogleMap googleMap) {
        onLocationLoaded(googleMap, googleMap.getCameraPosition().target);
    }

    private void onLocationLoaded(GoogleMap googleMap, LatLng latLng) {
        if (latLng == null) {
            return;
        }
        googleMap.clear();
        CameraUpdate locationUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);

        googleMap.moveCamera(locationUpdate);
        googleMap.animateCamera(locationUpdate);
    }

    private void onClickMapFragment(LatLng view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            Intent pickerIntent = builder.build(Objects.requireNonNull(getActivity()));
            startActivityForResult(pickerIntent, REQUEST_CODE_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException ignored) {
        }
    }

    private void inflateNearbyStoreRecyclerView() {
        nearByStore.setHasFixedSize(true);

        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
        nearByStore.setLayoutAnimation(controller);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        nearByStore.setLayoutManager(layoutManager);

        adapter = new NearbyStoreAdapter(placeViewModel, this);
        nearByStore.setAdapter(adapter);
    }

    private void onLoadNearByStoreList(List<Store> storeList) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setStoreList(storeList);
        nearByStore.scheduleLayoutAnimation();
        mapFragment.getMapAsync(googleMap -> {
            googleMap.clear();

            for (Store store : storeList) {
                LatLng selectedLatLng = new LatLng(store.latitude, store.longitude);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(selectedLatLng)
                        .title(store.name);

                googleMap.addMarker(markerOptions).showInfoWindow();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != AppCompatActivity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_PLACE_PICKER:
                Place place = PlacePicker.getPlace(Objects.requireNonNull(getActivity()), data);
                mapFragment.getMapAsync(googleMap -> onLocationLoaded(googleMap, place.getLatLng()));
                break;
        }
    }
}
