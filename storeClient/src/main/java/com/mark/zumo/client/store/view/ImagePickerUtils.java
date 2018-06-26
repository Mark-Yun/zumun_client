/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view;

import android.support.v4.app.FragmentActivity;

import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

/**
 * Created by mark on 18. 6. 26.
 */
public class ImagePickerUtils {
    public static void showImagePickerStoreThumbnail(FragmentActivity activity, int requestCode) {
        GalleryConfig config = new GalleryConfig.Build()
                .limitPickPhoto(1)
                .singlePhoto(true)
                .hintOfPick("this is pick hint")
                .filterMimeTypes(new String[]{"image/jpeg"})
                .build();
        GalleryActivity.openActivity(activity, requestCode, config);
    }

    public static void showImagePickerStoreCoverImage(FragmentActivity activity, int requestCode) {
        GalleryConfig config = new GalleryConfig.Build()
                .limitPickPhoto(1)
                .singlePhoto(true)
                .hintOfPick("this is pick hint")
                .filterMimeTypes(new String[]{"image/jpeg"})
                .build();
        GalleryActivity.openActivity(activity, requestCode, config);
    }
}