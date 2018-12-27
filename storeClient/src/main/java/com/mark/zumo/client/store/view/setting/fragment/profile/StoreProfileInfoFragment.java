/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.permission.PermissionFragment;
import com.mark.zumo.client.store.viewmodel.StoreSettingViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 25.
 */
public class StoreProfileInfoFragment extends Fragment {

    public static final String TAG = "StoreProfileInfoFragment";

    @BindView(R.id.cover_image) AppCompatImageView coverImage;
    @BindView(R.id.thumbnail_image) AppCompatImageView thumbnailImage;
    @BindView(R.id.store_name) AppCompatTextView storeName;
    @BindView(R.id.address) AppCompatTextView address;
    @BindView(R.id.title) AppCompatTextView title;

    private SupportMapFragment mapFragment;
    private StoreSettingViewModel storeSettingViewModel;
    private Store store;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeSettingViewModel = ViewModelProviders.of(getActivity()).get(StoreSettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_store_profile, container, false);
        ButterKnife.bind(this, view);

        inflateStoreProfileView();
        inflateMapFragment();

        return view;
    }

    private void inflateStoreProfileView() {
        storeSettingViewModel.getCurrentStore().observe(this, this::onLoadStore);
    }

    private void onLoadStore(Store store) {
        this.store = store;

        GlideApp.with(this)
                .load(store.coverImageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(coverImage);

        GlideApp.with(this)
                .load(store.thumbnailUrl)
                .apply(GlideUtils.storeImageOptions())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(thumbnailImage);

        storeName.setText(store.name);
        title.setText(store.name);

        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(store.latitude, store.longitude, 1);
            if (addresses.size() > 0) {
                address.setText(addresses.get(0).getAddressLine(0));
            }
        } catch (IOException ignored) {
        }

        mapFragment.getMapAsync(googleMap -> onLocationChanged(googleMap, store));
    }

    private void inflateMapFragment() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!PermissionFragment.isGrantedPermissions(permissions)) {
            Fragment permissionFragment = PermissionFragment.instantiate(permissions, this::inflateMapFragment);

            getFragmentManager().beginTransaction()
                    .replace(R.id.map_fragment, permissionFragment)
                    .commit();

            return;
        }

        mapFragment = new SupportMapFragment();
        FragmentManager fragmentManager = getFragmentManager();
        Objects.requireNonNull(fragmentManager).beginTransaction()
                .replace(R.id.map_fragment, mapFragment)
                .commit();

        mapFragment.getMapAsync(this::onReadyMap);
    }

    private void onLocationChanged(GoogleMap googleMap, Store store) {
        if (store == null || googleMap == null) {
            return;
        }

        googleMap.clear();

        LatLng selectedLatLng = new LatLng(store.latitude, store.longitude);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(selectedLatLng)
                .title(store.name);

        googleMap.addMarker(markerOptions).showInfoWindow();
        CameraUpdate locationUpdate = CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15);

        googleMap.setBuildingsEnabled(true);
        googleMap.moveCamera(locationUpdate);
        googleMap.animateCamera(locationUpdate);
    }

    @SuppressLint("MissingPermission")
    private void onReadyMap(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        onLocationChanged(googleMap, store);
    }
}
