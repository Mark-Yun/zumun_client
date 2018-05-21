package com.mark.zumo.client.core.util.glide;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by mark on 18. 5. 19.
 */
public final class GlideUtils {
    private GlideUtils() {
        /* Empty Body */
    }

    public static RequestOptions storeImageOptions() {
        return RequestOptions.circleCropTransform()
                .format(DecodeFormat.PREFER_ARGB_8888);
    }

    public static DrawableTransitionOptions storeTransitionOptions() {
        return DrawableTransitionOptions.withCrossFade();
    }

    public static DrawableTransitionOptions storeCoverTransitionOptions() {
        return DrawableTransitionOptions.withCrossFade();
    }

    public static RequestOptions storeCoverImageOptions() {
        return RequestOptions.centerCropTransform()
                .format(DecodeFormat.PREFER_ARGB_8888);
    }

    public static RequestOptions menuImageOptions() {
        return RequestOptions.fitCenterTransform();
    }

    public static DrawableTransitionOptions menuTransitionOptions() {
        return DrawableTransitionOptions.withCrossFade();
    }
}
