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

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

/**
 * Created by mark on 18. 2. 3.
 */

public class ImageCropperUtils {
    private static final String TAG = "ImageCropperUtils";

    public static void showCoverImageCropper(Context context, Fragment fragment, String path) {
        Log.d(TAG, "showCoverImageCropper: " + path);
        File selectedFile = new File(path);
        Uri uri = Uri.fromFile(selectedFile);
        CropImage.activity(uri)
                .setAspectRatio(7, 4)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(context, fragment);
    }

    public static void showThumbnailImageCropper(Context context, Fragment fragment, String path) {
        Log.d(TAG, "showCoverImageCropper: " + path);
        File selectedFile = new File(path);
        Uri uri = Uri.fromFile(selectedFile);
        CropImage.activity(uri)
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(context, fragment);
    }
}
