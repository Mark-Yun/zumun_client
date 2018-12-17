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

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.menu.create;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.util.ImageCropperUtils;
import com.mark.zumo.client.store.view.util.ImagePickerUtils;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 7. 31.
 */
public class MenuCreateSettingFragment extends Fragment {

    public final static String KEY_MENU_UUID = "menu_uuid";

    private static final int REQUEST_CODE_IMAGE_PICKER = 101;
    private static final String TAG = "MenuCreateSettingFragment";

    @BindView(R.id.image) AppCompatImageView image;

    private MenuSettingViewModel menuSettingViewModel;

    private MenuCreationListener menuCreationListener;
    private Runnable cancelListener;

    private Uri menuImageUri;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuSettingViewModel = ViewModelProviders.of(this).get(MenuSettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu_create, container, false);
        ButterKnife.bind(this, view);

        inflatePreferenceFragment();
        return view;
    }

    public MenuCreateSettingFragment onCreateMenu(MenuCreationListener menuCreationListener) {
        this.menuCreationListener = menuCreationListener;
        return this;
    }

    public MenuCreateSettingFragment onCancel(Runnable cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }

    private void inflatePreferenceFragment() {
        Fragment fragment = PreferenceFragmentCompat.instantiate(getActivity(), MenuCreatePreferenceFragment.class.getName(), getArguments());
        getFragmentManager().beginTransaction()
                .replace(R.id.menu_detail_preference_fragment, fragment)
                .commit();
    }

    @OnClick(R.id.image)
    void onClickCoverImage() {
        ImagePickerUtils.showMenuImagePicker(getActivity(), REQUEST_CODE_IMAGE_PICKER);
    }

    @SuppressWarnings("unchecked")
    private void onImagePicked(Intent data, int requestCode) {
        Serializable serializableExtra = data.getSerializableExtra(GalleryActivity.PHOTOS);
        List<String> photoList = (List<String>) serializableExtra;
        if (photoList == null || photoList.size() == 0) {
            return;
        }

        String selectedPath = photoList.get(0);
        if (requestCode == REQUEST_CODE_IMAGE_PICKER) {
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

        menuImageUri = result.getUri();

        GlideApp.with(this)
                .load(menuImageUri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != AppCompatActivity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_IMAGE_PICKER:
                onImagePicked(data, requestCode);
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                onImageCopped(resultCode, data);
                break;
        }
    }

    public interface MenuCreationListener {
        void onMenuCreate(Menu menu);
    }
}
