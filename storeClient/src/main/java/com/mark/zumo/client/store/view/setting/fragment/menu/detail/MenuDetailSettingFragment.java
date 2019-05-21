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

package com.mark.zumo.client.store.view.setting.fragment.menu.detail;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.util.ImageCropperUtils;
import com.mark.zumo.client.store.view.util.ImagePickerUtils;
import com.mark.zumo.client.store.viewmodel.MenuOptionSettingViewModel;
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
public class MenuDetailSettingFragment extends Fragment {

    public final static String KEY_MENU_UUID = "menu_uuid";

    private static final int REQUEST_CODE_IMAGE_PICKER = 101;
    private static final String TAG = "MenuDetailSettingFragment";

    @BindView(R.id.image) AppCompatImageView image;
    @BindView(R.id.menu_name) AppCompatTextView menuName;
    @BindView(R.id.menu_price) AppCompatTextView menuPrice;
    @BindView(R.id.category_recycler_view) RecyclerView categoryRecyclerView;
    @BindView(R.id.option_recycler_view) RecyclerView optionRecyclerView;
    @BindView(R.id.menu_info_layout) LinearLayoutCompat menuInfoLayout;

    private MenuSettingViewModel menuSettingViewModel;
    private MenuOptionSettingViewModel menuOptionSettingViewModel;
    private String menuUuid;

    private MenuUpdateListener menuUpdateListener;

    public static MenuDetailSettingFragment newInstance(String menuUuid) {

        Bundle args = new Bundle();
        args.putString(KEY_MENU_UUID, menuUuid);

        MenuDetailSettingFragment fragment = new MenuDetailSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MenuDetailSettingFragment onMenuUpdated(MenuUpdateListener menuUpdateListener) {
        this.menuUpdateListener = menuUpdateListener;
        return this;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuUuid = getArguments().getString(KEY_MENU_UUID);
        menuSettingViewModel = ViewModelProviders.of(this).get(MenuSettingViewModel.class);
        menuOptionSettingViewModel = ViewModelProviders.of(this).get(MenuOptionSettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu_detail, container, false);
        ButterKnife.bind(this, view);

        inflateMenuImage();
        inflateCategoryRecyclerView();
        inflateOptionRecyclerView();

        return view;
    }

    private void inflateCategoryRecyclerView() {
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoryRecyclerView.setHasFixedSize(false);
        categoryRecyclerView.setNestedScrollingEnabled(false);

        MenuDetailMenuCategoryAdapter menuDetailMenuCategoryAdapter = new MenuDetailMenuCategoryAdapter();
        categoryRecyclerView.setAdapter(menuDetailMenuCategoryAdapter);
        menuSettingViewModel.getMenuCategoryListByMenuUuid(menuUuid).observe(this, menuDetailMenuCategoryAdapter::setMenuCategoryList);
    }

    private void inflateOptionRecyclerView() {
        optionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        optionRecyclerView.setHasFixedSize(false);
        optionRecyclerView.setNestedScrollingEnabled(false);

        MenuDetailMenuOptionAdapter menuDetailMenuOptionAdapter = new MenuDetailMenuOptionAdapter();
        optionRecyclerView.setAdapter(menuDetailMenuOptionAdapter);
        menuOptionSettingViewModel.getMenuOptionCategoryListByMenuUuid(menuUuid).observe(this, menuDetailMenuOptionAdapter::setMenuOptionCategory);

    }

    private void inflateMenuImage() {
        menuSettingViewModel.getMenuFromDisk(menuUuid).observe(this, this::onLoadMenu);
    }

    private void onLoadMenu(Menu menu) {
        menuName.setText(menu.name);
        menuPrice.setText(String.valueOf(menu.price));

        GlideApp.with(this)
                .load(menu.imageUrl)
                .apply(GlideUtils.menuImageOptions())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image);
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

        String menuUuid = getArguments().getString(KEY_MENU_UUID);
        Uri resultUri = result.getUri();
        menuSettingViewModel.uploadAndUpdateMenuImage(getActivity(), menuUuid, resultUri).observe(this, this::onSuccessMenuUpdate);
    }

    private void onSuccessMenuUpdate(Menu menu) {
        getFragmentManager().popBackStack();
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


    @OnClick({R.id.menu_name, R.id.menu_name_edit})
    public void onMenuNameEditClicked(View view) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        AppCompatEditText editText = new AppCompatEditText(context);

        new AlertDialog.Builder(context)
                .setTitle(R.string.menu_name_update_dialog_title)
                .setMessage(R.string.menu_name_update_dialog_message)
                .setView(editText)
                .setCancelable(true)
                .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.button_text_apply, (dialog, which) -> {
                    menuSettingViewModel.updateMenuName(menuUuid, editText.getText() == null
                            ? ""
                            : editText.getText().toString())
                            .observe(this, this::onUpdateMenu);
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @OnClick({R.id.menu_price, R.id.menu_price_edit})
    public void onMenuPriceEditClicked(View view) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        AppCompatEditText editText = new AppCompatEditText(context);
        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(context)
                .setTitle(R.string.menu_price_update_dialog_title)
                .setMessage(R.string.menu_price_update_dialog_message)
                .setView(editText)
                .setCancelable(true)
                .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.button_text_apply, (dialog, which) -> {
                    menuSettingViewModel.updateMenuPrice(menuUuid, editText.getText() == null
                            ? 0
                            : Integer.parseInt(editText.getText().toString()))
                            .observe(this, this::onUpdateMenu);
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void onUpdateMenu(Menu menu) {
        onLoadMenu(menu);
        menuUpdateListener.onMenuUpdated(menu);
    }

    public interface MenuUpdateListener {
        void onMenuUpdated(Menu menu);
    }
}
