/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.aws;

import android.content.Context;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 19. 5. 12.
 */
public enum AWSAppSyncClientRepository {
    INSTNACE;

    private AWSAppSyncClient awsAppSyncClient;

    public Maybe<AWSAppSyncClient> awsAppSyncClientMaybe(final Context context) {
        if (awsAppSyncClient == null) {
            return Maybe.fromCallable(() -> createBuilder(context))
                    .map(AWSAppSyncClient.Builder::build)
                    .subscribeOn(Schedulers.computation())
                    .doOnSuccess(this::setAwsAppSyncClient);
        }

        return Maybe.just(awsAppSyncClient);
    }

    private void setAwsAppSyncClient(final AWSAppSyncClient awsAppSyncClient) {
        this.awsAppSyncClient = awsAppSyncClient;
    }

    private AWSAppSyncClient.Builder createBuilder(final Context context) {
        return AWSAppSyncClient.builder()
                .context(context)
                .awsConfiguration(new AWSConfiguration(context));
    }

}
