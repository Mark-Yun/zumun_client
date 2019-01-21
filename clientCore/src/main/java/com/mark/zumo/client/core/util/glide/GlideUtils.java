/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.util.glide;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mark.zumo.client.core.R;
import com.mark.zumo.client.core.util.context.ContextHolder;

/**
 * Created by mark on 18. 5. 19.
 */
public final class GlideUtils {
    private GlideUtils() {
        /* Empty Body */
    }

    public static RequestOptions storeThumbnailImageOptions() {
        return RequestOptions.circleCropTransform();
    }

    public static DrawableTransitionOptions storeThumbnailTransitionOptions() {
        return DrawableTransitionOptions.withCrossFade();
    }

    public static RequestOptions cartMenuImageOptions() {
        return RequestOptions.circleCropTransform();
    }

    public static DrawableTransitionOptions cartMenuTransitionOptions() {
        return DrawableTransitionOptions.withCrossFade();
    }

    public static DrawableTransitionOptions storeCoverTransitionOptions() {
        return DrawableTransitionOptions.withCrossFade();
    }

    public static RequestOptions storeCoverImageOptions() {
        return RequestOptions.centerCropTransform();
    }

    public static RequestOptions menuImageOptions() {
        int dimensionPixelSize = ContextHolder.getContext().getResources().getDimensionPixelSize(R.dimen.menu_round_corners);
        return new RequestOptions()
                .transforms(new CenterCrop(), new RoundedCorners(dimensionPixelSize));
    }

    public static DrawableTransitionOptions menuTransitionOptions() {
        return DrawableTransitionOptions.withCrossFade();
    }
}
