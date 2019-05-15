/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.aws;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;

import io.reactivex.Maybe;

/**
 * Created by mark on 19. 5. 12.
 */
public enum AWSMobileClientRepository {
    INSTANCE;

    private static final String TAG = "AWSMobileClientRepository";
    private boolean isInitialized;

    public Maybe<AWSMobileClient> awsMobileClientMaybe(final Context context) {
        return Maybe.create(emitter -> {
            if (isInitialized) {
                emitter.onSuccess(AWSMobileClient.getInstance());
                emitter.onComplete();
            } else {
                AWSMobileClient.getInstance().initialize(context, awsStartupResult -> {
                    Log.i(TAG, "awsMobileClientMaybe: complete! awsStartupResult=" + awsStartupResult.toString());
                    isInitialized = true;
                    emitter.onSuccess(AWSMobileClient.getInstance());
                    emitter.onComplete();
                }).execute();
            }
        });
    }
}
