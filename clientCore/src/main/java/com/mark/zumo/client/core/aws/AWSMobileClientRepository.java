/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.aws;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;

import io.reactivex.Maybe;

/**
 * Created by mark on 19. 5. 12.
 */
public enum AWSMobileClientRepository {
    INSTANCE;

    private static final String TAG = "AWSMobileClientRepository";
    private boolean isInitialized;

    AWSMobileClientRepository() {
    }

    public Maybe<AWSMobileClient> awsMobileClientMaybe(final Context context) {
        return Maybe.create(emitter -> {
            if (isInitialized) {
                emitter.onSuccess(AWSMobileClient.getInstance());
                emitter.onComplete();
            } else {
                AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails userStateDetails) {
                        Log.i(TAG, "AWSMobileClient initialized. User State is " + userStateDetails.getUserState());
                        isInitialized = true;
                        emitter.onSuccess(AWSMobileClient.getInstance());
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Initialization error.", e);
                        emitter.onError(e);
                        emitter.onComplete();
                    }
                });
            }
        });
    }
}
