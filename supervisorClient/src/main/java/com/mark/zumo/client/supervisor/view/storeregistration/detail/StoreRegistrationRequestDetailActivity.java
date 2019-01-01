/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.supervisor.view.storeregistration.detail;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.supervisor.R;
import com.mark.zumo.client.supervisor.view.permission.PermissionFragment;
import com.mark.zumo.client.supervisor.viewmodel.StoreRegistrationViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 12. 30.
 */
public class StoreRegistrationRequestDetailActivity extends AppCompatActivity {

    private static final String STORE_REGISTRATION_REQUEST_UUID_KEY = "store_registration_request_uuid";

    @BindView(R.id.store_name) TextInputEditText storeName;
    @BindView(R.id.store_phone_number) TextInputEditText storePhoneNumber;
    @BindView(R.id.store_type) TextInputEditText storeType;
    @BindView(R.id.store_address) TextInputEditText storeAddress;

    @BindView(R.id.corporate_registration_scan_image_view) AppCompatImageView corporateRegistrationScanImageView;
    @BindView(R.id.corporate_registration_name) TextInputEditText corporateRegistrationName;
    @BindView(R.id.corporate_registration_owner_name) TextInputEditText corporateRegistrationOwnerName;
    @BindView(R.id.corporate_registration_number) TextInputEditText corporateRegistrationNumber;
    @BindView(R.id.corporate_registration_address) TextInputEditText corporateRegistrationAddress;

    private StoreRegistrationViewModel storeRegistrationViewModel;

    public static void start(Context context, String uuid) {
        Intent intent = new Intent(context, StoreRegistrationRequestDetailActivity.class);
        intent.putExtra(StoreRegistrationRequestDetailActivity.STORE_REGISTRATION_REQUEST_UUID_KEY, uuid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_store_registration_request_detail);
        ButterKnife.bind(this);

        storeRegistrationViewModel = ViewModelProviders.of(this).get(StoreRegistrationViewModel.class);

        inflateView();
    }

    private void inflateView() {
        String uuid = getIntent().getStringExtra(STORE_REGISTRATION_REQUEST_UUID_KEY);
        storeRegistrationViewModel.getStoreRegistrationRequestByUuidFromDisk(uuid).observe(this, this::onLoadStoreRegistrationRequest);
    }

    private void onLoadStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest) {
        storeName.setText(storeRegistrationRequest.storeName);
        storePhoneNumber.setText(storeRegistrationRequest.storePhoneNumber);
        storeType.setText(storeRegistrationRequest.storeType);
        storeAddress.setText(storeRegistrationRequest.storeAddress);
        corporateRegistrationName.setText(storeRegistrationRequest.corporateRegistrationName);
        corporateRegistrationOwnerName.setText(storeRegistrationRequest.corporateRegistrationOwnerName);
        corporateRegistrationNumber.setText(storeRegistrationRequest.corporateRegistrationNumber);
        corporateRegistrationAddress.setText(storeRegistrationRequest.corporateRegistrationAddress);

        GlideApp.with(this)
                .load(storeRegistrationRequest.corporateRegistrationScanUrl)
                .into(corporateRegistrationScanImageView);

        corporateRegistrationScanImageView.setOnClickListener(v -> CorporateScanImageActivity.startImageDetail(this, storeRegistrationRequest.corporateRegistrationScanUrl));

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!PermissionFragment.isGrantedPermissions(permissions)) {
            Fragment permissionFragment = PermissionFragment.instantiate(permissions, () -> inflateMapFragment(storeRegistrationRequest))
                    .setTitle(R.string.permission_location_title)
                    .setMessage(R.string.permission_location_content);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map_fragment, permissionFragment)
                    .commit();
        } else {
            inflateMapFragment(storeRegistrationRequest);
        }
    }

    private void inflateMapFragment(final StoreRegistrationRequest storeRegistrationRequest) {
        SupportMapFragment mapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction()
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

    @OnClick(R.id.ok)
    void OnClickOk() {
        final String requestUuid = getIntent().getStringExtra(STORE_REGISTRATION_REQUEST_UUID_KEY);
        new AlertDialog.Builder(this)
                .setTitle("Accept Request")
                .setMessage("accept request?")
                .setPositiveButton(android.R.string.ok, (dialog, what) -> storeRegistrationViewModel.approve(requestUuid).observe(this, this::onActionResult))
                .setNegativeButton(android.R.string.cancel, (dialog, what) -> dialog.dismiss())
                .create()
                .show();
    }

    @OnClick(R.id.cancel)
    void OnClickCancel() {
        final String requestUuid = getIntent().getStringExtra(STORE_REGISTRATION_REQUEST_UUID_KEY);
        final AppCompatEditText editText = new AppCompatEditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Reject Request")
                .setMessage("reject request?\n Input Reject Comment")
                .setView(editText)
                .setPositiveButton(android.R.string.ok, (dialog, what) -> storeRegistrationViewModel.reject(requestUuid, editText.getText().toString()).observe(this, this::onActionResult))
                .setNegativeButton(android.R.string.cancel, (dialog, what) -> dialog.dismiss())
                .create()
                .show();
    }

    private void onActionResult(StoreRegistrationResult storeRegistrationResult) {
        new AlertDialog.Builder(this)
                .setTitle("Result")
                .setMessage(storeRegistrationResult.result)
                .setNeutralButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                }).create()
                .show();
    }
}
