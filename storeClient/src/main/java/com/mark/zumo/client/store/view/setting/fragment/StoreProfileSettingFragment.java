/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.util.ImageCropperUtils;
import com.mark.zumo.client.store.view.util.ImagePickerUtils;
import com.mark.zumo.client.store.viewmodel.StoreSettingViewModel;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 6. 25.
 */
public class StoreProfileSettingFragment extends Fragment {

    public static final String TAG = "StoreProfileSettingFragment";
    private static final int REQUEST_CODE_PLACE_PICKER = 15;
    private static final int REQUEST_CODE_COVER_IMAGE_PICKER = 11;
    private static final int REQUEST_CODE_THUMBNAIL_IMAGE_PICKER = 12;

    @BindView(R.id.cover_image) AppCompatImageView coverImage;
    @BindView(R.id.thumbnail_image) AppCompatImageView thumbnailImage;
    @BindView(R.id.store_name) AppCompatTextView storeName;
    @BindView(R.id.address) AppCompatTextView address;

    private SupportMapFragment mapFragment;
    private StoreSettingViewModel storeSettingViewModel;
    private Store store;
    private int requestCode;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeSettingViewModel = ViewModelProviders.of(this).get(StoreSettingViewModel.class);
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
                .into(coverImage);

        GlideApp.with(this)
                .load(store.thumbnailUrl)
                .apply(GlideUtils.storeImageOptions())
                .into(thumbnailImage);

        storeName.setText(store.name);

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

    @OnClick(R.id.store_name)
    public void onStoreNameClicked() {
        AppCompatEditText editText = new AppCompatEditText(Objects.requireNonNull(getActivity()));
        editText.setText(storeName.getText().toString());
        editText.selectAll();

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.store_profile_name_editor_title)
                .setMessage(R.string.store_profile_name_editor_message)
                .setView(editText)
                .setCancelable(true)
                .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.button_text_apply, (dialog, which) -> {
                    storeSettingViewModel.updateStoreName(editText.getText().toString())
                            .observe(this, this::onLoadStore);
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void inflateMapFragment() {
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
        CameraUpdate locationUpdate = CameraUpdateFactory.newLatLngZoom(selectedLatLng, REQUEST_CODE_PLACE_PICKER);

        googleMap.setBuildingsEnabled(true);
        googleMap.moveCamera(locationUpdate);
        googleMap.animateCamera(locationUpdate);
    }

    @SuppressLint("MissingPermission")
    private void onReadyMap(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapClickListener(this::onClickMapFragment);
        onLocationChanged(googleMap, store);
    }

    private void onClickMapFragment(LatLng view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            Intent pickerIntent = builder.build(Objects.requireNonNull(getActivity()));
            startActivityForResult(pickerIntent, REQUEST_CODE_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException ignored) {
        }
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
                storeSettingViewModel.updateStoreLocation(place.getLatLng()).observe(this, this::onLoadStore);
                break;

            case REQUEST_CODE_COVER_IMAGE_PICKER:
                onImagePicked(data, requestCode);
                break;

            case REQUEST_CODE_THUMBNAIL_IMAGE_PICKER:
                onImagePicked(data, requestCode);
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                onImageCopped(resultCode, data);
                break;
        }
    }

    @OnClick(R.id.cover_image)
    void onClickCoverImage() {
        ImagePickerUtils.showImagePickerStoreCoverImage(getActivity(), REQUEST_CODE_COVER_IMAGE_PICKER);
    }

    @OnClick(R.id.thumbnail_image)
    void onClickThumbnailImage() {
        ImagePickerUtils.showImagePickerStoreThumbnail(getActivity(), REQUEST_CODE_THUMBNAIL_IMAGE_PICKER);
    }

    @SuppressWarnings("unchecked")
    private void onImagePicked(Intent data, int requestCode) {
        Serializable serializableExtra = data.getSerializableExtra(GalleryActivity.PHOTOS);
        List<String> photoList = (List<String>) serializableExtra;
        if (photoList == null || photoList.size() == 0) {
            return;
        }

        String selectedPath = photoList.get(0);
        this.requestCode = requestCode;

        if (requestCode == REQUEST_CODE_COVER_IMAGE_PICKER) {
            ImageCropperUtils.showCoverImageCropper(getActivity(), this, selectedPath);
        } else if (requestCode == REQUEST_CODE_THUMBNAIL_IMAGE_PICKER) {
            ImageCropperUtils.showThumbnailImageCropper(getActivity(), this, selectedPath);
        }
    }

    private void onImageCopped(int resultCode, Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);

        if (resultCode != AppCompatActivity.RESULT_OK) {
            Exception error = result.getError();
            Log.e(TAG, "onImageCopped: ", error);
            return;
        }

        Uri resultUri = result.getUri();
        if (this.requestCode == REQUEST_CODE_THUMBNAIL_IMAGE_PICKER) {
            storeSettingViewModel.uploadThumbnailImage(getActivity(), resultUri)
                    .observe(this, this::onLoadStore);
        } else if (this.requestCode == REQUEST_CODE_COVER_IMAGE_PICKER) {
            storeSettingViewModel.uploadCoverImage(getActivity(), resultUri)
                    .observe(this, this::onLoadStore);
        }

    }
}
