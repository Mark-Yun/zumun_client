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

package com.mark.zumo.client.store.view.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.mark.zumo.client.core.util.context.ContextHolder;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

/**
 * Created by mark on 18. 6. 26.
 */
public class ImagePickerUtils {

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static boolean hasPermission() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(ContextHolder.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void showMenuImagePicker(FragmentActivity activity, int requestCode) {
        GalleryConfig config = new GalleryConfig.Build()
                .singlePhoto(true)
                .hintOfPick("this is pick hint")
                .filterMimeTypes(new String[]{"image"})
                .build();
        GalleryActivity.openActivity(activity, requestCode, config);
    }

    public static void showCorporateScanImagePicker(FragmentActivity activity, int requestCode) {
        GalleryConfig config = new GalleryConfig.Build()
                .singlePhoto(true)
                .hintOfPick("this is pick hint")
                .filterMimeTypes(new String[]{"image"})
                .build();
        GalleryActivity.openActivity(activity, requestCode, config);
    }

    public static void showImagePickerStoreThumbnail(FragmentActivity activity, int requestCode) {
        GalleryConfig config = new GalleryConfig.Build()
                .singlePhoto(true)
                .hintOfPick("this is pick hint")
                .filterMimeTypes(new String[]{"image"})
                .build();
        GalleryActivity.openActivity(activity, requestCode, config);
    }

    public static void showImagePickerStoreCoverImage(FragmentActivity activity, int requestCode) {
        GalleryConfig config = new GalleryConfig.Build()
                .singlePhoto(true)
                .hintOfPick("this is pick hint")
                .filterMimeTypes(new String[]{"image"})
                .build();
        GalleryActivity.openActivity(activity, requestCode, config);
    }
}