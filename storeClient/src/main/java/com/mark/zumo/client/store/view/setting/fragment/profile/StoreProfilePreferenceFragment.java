/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.fragment.BasePreferenceFragmentCompat;
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

/**
 * Created by mark on 18. 10. 28.
 */
public class StoreProfilePreferenceFragment extends BasePreferenceFragmentCompat {

    private static final String TAG = "StoreProfilePreferenceFragment";

    private static final int REQUEST_CODE_PLACE_PICKER = 15;
    private static final int REQUEST_CODE_COVER_IMAGE_PICKER = 11;
    private static final int REQUEST_CODE_THUMBNAIL_IMAGE_PICKER = 12;

    private int requestCode;
    private StoreSettingViewModel storeSettingViewModel;

    private StoreUpdateListener storeUpdateListener;
    private EditTextPreference storeNamePreference;
    private Preference coverImagePreference;
    private Preference thumbnailImagePreference;
    private Preference addressPreference;

    public static StoreProfilePreferenceFragment newInstance() {

        Bundle args = new Bundle();

        StoreProfilePreferenceFragment fragment = new StoreProfilePreferenceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public StoreProfilePreferenceFragment onStoreUpdated(StoreUpdateListener storeUpdateListener) {
        this.storeUpdateListener = storeUpdateListener;
        return this;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeSettingViewModel = ViewModelProviders.of(this).get(StoreSettingViewModel.class);
    }

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.pref_screen_store_profile);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflateStore();
        return view;
    }

    private void inflateStore() {
        storeNamePreference = (EditTextPreference) findPreference(getString(R.string.store_name_preference_key));
        storeNamePreference.setOnPreferenceChangeListener(this::onStoreNameChanged);

        coverImagePreference = findPreference(getString(R.string.store_cover_image_preference_key));
        coverImagePreference.setOnPreferenceClickListener(this::onCoverImagePreferenceClicked);

        thumbnailImagePreference = findPreference(getString(R.string.store_thumbnail_image_preference_key));
        thumbnailImagePreference.setOnPreferenceClickListener(this::onThumbnailImagePreferenceClicked);

        addressPreference = findPreference(getString(R.string.store_address_preference_key));
        addressPreference.setOnPreferenceClickListener(this::onAddressPreferenceClicked);

        storeSettingViewModel.getCurrentSessionStore().observe(this, this::onLoadStore);
    }

    private void onLoadStore(Store store) {
        storeNamePreference.setSummary(store.name);

        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(store.latitude, store.longitude, 1);
            if (addresses.size() > 0) {
                addressPreference.setSummary(addresses.get(0).getAddressLine(0));
            }
        } catch (IOException ignored) {
        }
    }

    private boolean onStoreNameChanged(Preference preference, Object object) {
        storeSettingViewModel.updateStoreName(object.toString()).observe(this, this::onStoreUpdate);
        return true;
    }

    private void onStoreUpdate(Store store) {
        onLoadStore(store);
        storeUpdateListener.onStoreUpdate(store);
    }

    private boolean onCoverImagePreferenceClicked(Preference preference) {
        ImagePickerUtils.showImagePickerStoreCoverImage(getActivity(), REQUEST_CODE_COVER_IMAGE_PICKER);
        return true;
    }

    private boolean onThumbnailImagePreferenceClicked(Preference preference) {
        ImagePickerUtils.showImagePickerStoreThumbnail(getActivity(), REQUEST_CODE_THUMBNAIL_IMAGE_PICKER);
        return true;
    }

    private boolean onAddressPreferenceClicked(Preference preference) {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            Intent pickerIntent = builder.build(Objects.requireNonNull(getActivity()));
            startActivityForResult(pickerIntent, REQUEST_CODE_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException ignored) {
        }

        return true;
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
                storeSettingViewModel.updateStoreLocation(place.getLatLng()).observe(this, this::onStoreUpdate);
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
                    .observe(this, this::onStoreUpdate);
        } else if (this.requestCode == REQUEST_CODE_COVER_IMAGE_PICKER) {
            storeSettingViewModel.uploadCoverImage(getActivity(), resultUri)
                    .observe(this, this::onStoreUpdate);
        }

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

    interface StoreUpdateListener {
        void onStoreUpdate(Store store);
    }
}
