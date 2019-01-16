/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.store.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationException;
import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.permission.PermissionFragment;
import com.mark.zumo.client.store.view.util.ImagePickerUtils;
import com.mark.zumo.client.store.viewmodel.StoreRegistrationViewModel;
import com.tangxiaolv.telegramgallery.GalleryActivity;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 11. 6.
 */
public class StoreRegistrationCreateFragment extends Fragment {

    private static final String TAG = "StoreRegistrationCreateFragment";

    private static final int REQUEST_CODE_CORPORATE_REGISTRATION_SCAN_IMAGE_PICKER = 55151;

    @BindView(R.id.store_name) TextInputEditText storeName;
    @BindView(R.id.store_phone_number) TextInputEditText storePhoneNumber;
    @BindView(R.id.store_type) TextInputEditText storeType;
    @BindView(R.id.store_address) TextInputEditText storeAddress;
    @BindView(R.id.corporate_registration_scan_image_view) AppCompatImageView corporateRegistrationScanImageView;
    @BindView(R.id.corporate_registration_name) TextInputEditText corporateRegistrationName;
    @BindView(R.id.corporate_registration_owner_name) TextInputEditText corporateRegistrationOwnerName;
    @BindView(R.id.corporate_registration_number) TextInputEditText corporateRegistrationNumber;
    @BindView(R.id.corporate_registration_scan_hint_icon) AppCompatImageView corporateRegistrationScanHintIcon;
    @BindView(R.id.corporate_registration_scan_hint) AppCompatTextView corporateRegistrationScanHint;

    private StoreRegistrationViewModel storeRegistrationViewModel;
    private CreateRequestSuccessListener listener;

    private String selectedScanImagePath;
    private Location location;

    public static StoreRegistrationCreateFragment newInstance() {

        Bundle args = new Bundle();

        StoreRegistrationCreateFragment fragment = new StoreRegistrationCreateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeRegistrationViewModel = ViewModelProviders.of(this).get(StoreRegistrationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_registration_up, container, false);
        ButterKnife.bind(this, view);

        inflateMapFragment();
        return view;
    }

    private void inflateMapFragment() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!PermissionFragment.isGrantedPermissions(permissions)) {
            Fragment permissionFragment = PermissionFragment.instantiate(permissions, this::inflateMapFragment)
                    .setTitle(R.string.permission_location_title)
                    .setMessage(R.string.permission_location_content);

            getFragmentManager().beginTransaction()
                    .replace(R.id.map_fragment, permissionFragment)
                    .commit();

            return;
        }

        SupportMapFragment mapFragment = new SupportMapFragment();

        getFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, mapFragment)
                .commit();

        mapFragment.getMapAsync(this::onReadyMap);
    }

    @SuppressLint("MissingPermission")
    private void onReadyMap(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);

        storeRegistrationViewModel.getCurrentLocation().observe(this, location -> onLocationChanged(googleMap, location));
    }

    private void onLocationChanged(GoogleMap googleMap, Location location) {
        if (location == null) {
            return;
        }

        this.location = location;

        LatLng selectedLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate locationUpdate = CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15);

        googleMap.moveCamera(locationUpdate);
        googleMap.animateCamera(locationUpdate);

        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(selectedLatLng.latitude, selectedLatLng.longitude, 1);
            if (addresses.size() > 0) {
                storeAddress.setText(addresses.get(0).getAddressLine(0));
            }
        } catch (IOException ignored) {
        }
    }

    @OnClick(R.id.ok)
    void onClickOk() {
        clearError();

        try {
            StoreRegistrationRequest storeRegistrationRequest = new StoreRegistrationRequest.Builder()
                    .setStoreName(storeName.getText().toString())
                    .setStorePhoneNumber(storePhoneNumber.getText().toString())
                    .setStoreType(storeType.getText().toString())
                    .setStoreAddress(storeAddress.getText().toString())
                    .setLatitude(location.getLatitude())
                    .setLongitude(location.getLongitude())
                    .setCorporateRegistrationScanUrl(selectedScanImagePath)
                    .setCorporateRegistrationName(corporateRegistrationName.getText().toString())
                    .setCorporateRegistrationOwnerName(corporateRegistrationOwnerName.getText().toString())
                    .setCorporateRegistrationNumber(corporateRegistrationNumber.getText().toString())
                    .setCorporateRegistrationAddress(storeName.getText().toString())
                    .build();

            storeRegistrationViewModel.createStoreRegistrationRequest(getActivity(), storeRegistrationRequest)
                    .observe(this, listener::onSuccessCreateRequest);
        } catch (StoreRegistrationException e) {
            Log.e(TAG, "onClickOk: ", e);
            handleStoreRegistrationException(e);
        }
    }

    private void clearError() {
        storeName.setError(null);
        storePhoneNumber.setError(null);
        storeType.setError(null);
        storeAddress.setError(null);
        corporateRegistrationName.setError(null);
        corporateRegistrationOwnerName.setError(null);
        corporateRegistrationNumber.setError(null);
        storeAddress.setError(null);
    }

    private void handleStoreRegistrationException(StoreRegistrationException e) {
        String errorMessage = e.message;
        switch (e.storeUserSignupErrorCode) {
            case EMPTY_STORE_NAME:
                storeName.setError(errorMessage);
                break;
            case EMPTY_STORE_PHONE_NUMBER:
                storePhoneNumber.setError(errorMessage);
                break;
            case EMPTY_STORE_TYPE:
                storeType.setError(errorMessage);
                break;
            case EMPTY_EMPTY_ADDRESS:
                storeAddress.setError(errorMessage);
                break;
            case EMPTY_CORPORATE_REGISTRATION_NAME:
                corporateRegistrationName.setError(errorMessage);
                break;
            case EMPTY_CORPORATE_REGISTRATION_OWNER_NAME:
                corporateRegistrationOwnerName.setError(errorMessage);
                break;
            case EMPTY_CORPORATE_REGISTRATION_NUMBER:
                corporateRegistrationNumber.setError(errorMessage);
                break;
            case EMPTY_CORPORATE_REGISTRATION_ADDRESS:
                storeAddress.setError(errorMessage);
                break;
            case EMPTY_CORPORATE_REGISTRATION_SCAN_URL:
//                storeName.setError(errorMessage);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public StoreRegistrationCreateFragment doOnCreateRequestSuccess(CreateRequestSuccessListener listener) {
        this.listener = listener;
        return this;
    }

    @OnClick(R.id.corporate_registration_scan_image_view)
    void onClickCorporateRegistrationScanImageViewClicked() {
        ImagePickerUtils.showCorporateScanImagePicker(getActivity(), REQUEST_CODE_CORPORATE_REGISTRATION_SCAN_IMAGE_PICKER);
    }

    @SuppressWarnings("unchecked")
    private void onImagePicked(Intent data, int requestCode) {
        Serializable serializableExtra = data.getSerializableExtra(GalleryActivity.PHOTOS);
        List<String> photoList = (List<String>) serializableExtra;
        if (photoList == null || photoList.size() == 0) {
            return;
        }

        if (requestCode == REQUEST_CODE_CORPORATE_REGISTRATION_SCAN_IMAGE_PICKER) {

            selectedScanImagePath = photoList.get(0);

            GlideApp.with(this)
                    .load(selectedScanImagePath)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(corporateRegistrationScanImageView);

            corporateRegistrationScanHintIcon.setVisibility(View.GONE);
            corporateRegistrationScanHint.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != AppCompatActivity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_CORPORATE_REGISTRATION_SCAN_IMAGE_PICKER:
                onImagePicked(data, requestCode);
                break;
        }
    }

    public interface CreateRequestSuccessListener {
        void onSuccessCreateRequest(StoreRegistrationRequest storeRegistrationRequest);
    }
}
