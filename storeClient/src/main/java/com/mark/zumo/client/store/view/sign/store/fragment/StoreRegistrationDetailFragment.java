/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.store.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.store.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 12. 26.
 */
public class StoreRegistrationDetailFragment extends Fragment {

    @BindView(R.id.store_name) AppCompatTextView storeName;
    @BindView(R.id.store_phone_number) AppCompatTextView storePhoneNumber;
    @BindView(R.id.store_type) AppCompatTextView storeType;
    @BindView(R.id.map_fragment) ConstraintLayout mapFragment;
    @BindView(R.id.store_address) AppCompatTextView storeAddress;
    @BindView(R.id.corporate_registration_scan_image_view) AppCompatImageView corporateRegistrationScanImageView;
    @BindView(R.id.corporate_name) AppCompatTextView corporateName;
    @BindView(R.id.corporate_owner_name) AppCompatTextView corporateOwnerName;
    @BindView(R.id.corporate_number) AppCompatTextView corporateNumber;
    @BindView(R.id.corporate_address) AppCompatTextView corporateAddress;

    private StoreRegistrationRequest storeRegistrationRequest;

    public static StoreRegistrationDetailFragment newInstance() {

        Bundle args = new Bundle();

        StoreRegistrationDetailFragment fragment = new StoreRegistrationDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_registration_detail, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    public StoreRegistrationDetailFragment setStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest) {
        this.storeRegistrationRequest = storeRegistrationRequest;
        return this;
    }

    private void inflateView() {
        if (storeRegistrationRequest == null) {
            return;
        }

        storeName.setText(storeRegistrationRequest.storeName);
        storePhoneNumber.setText(storeRegistrationRequest.storePhoneNumber);
        storeType.setText(storeRegistrationRequest.storeType);
        storeAddress.setText(storeRegistrationRequest.storeAddress);
        corporateName.setText(storeRegistrationRequest.corporateRegistrationName);
        corporateOwnerName.setText(storeRegistrationRequest.corporateRegistrationOwnerName);
        corporateNumber.setText(storeRegistrationRequest.corporateRegistrationOwnerName);
        corporateAddress.setText(storeRegistrationRequest.corporateRegistrationAddress);

        GlideApp.with(this)
                .load(storeRegistrationRequest.corporateRegistrationScanUrl)
                .into(corporateRegistrationScanImageView);

        SupportMapFragment mapFragment = new SupportMapFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, mapFragment)
                .commit();

        mapFragment.getMapAsync(googleMap -> onReadyMap(googleMap, storeRegistrationRequest));
    }

    private void onReadyMap(GoogleMap googleMap, StoreRegistrationRequest registrationRequest) {
        if (googleMap == null || registrationRequest == null) {
            return;
        }

        googleMap.clear();

        LatLng selectedLatLng = new LatLng(registrationRequest.latitude, registrationRequest.longitude);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(selectedLatLng)
                .title(registrationRequest.storeName);

        googleMap.addMarker(markerOptions).showInfoWindow();
        CameraUpdate locationUpdate = CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15);

        googleMap.setBuildingsEnabled(true);
        googleMap.moveCamera(locationUpdate);
        googleMap.animateCamera(locationUpdate);
    }
}
