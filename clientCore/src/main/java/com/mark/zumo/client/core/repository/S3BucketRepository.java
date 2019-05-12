/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.mark.zumo.client.core.R;
import com.mark.zumo.client.core.aws.AWSMobileClientRepository;
import com.mark.zumo.client.core.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 19. 5. 12.
 */
public enum S3BucketRepository {
    INSTANCE;

    private static final String TAG = "S3BucketRepository";
    private static final String SCOPE = "public/";

    private final AWSMobileClientRepository awsMobileClientRepository;

    private TransferUtility transferUtility;

    S3BucketRepository() {
        awsMobileClientRepository = AWSMobileClientRepository.INSTANCE;
    }

    private Context startTransferService(final Context context) {
        context.startService(new Intent(context, TransferService.class));
        return context;
    }

    private Maybe<TransferUtility> transferUtilityMaybe(final Context context) {
        if (transferUtility == null) {
            return Maybe.just(context)
                    .map(this::startTransferService)
                    .flatMap(this::createTransferUtilityBuilder)
                    .map(TransferUtility.Builder::build)
                    .subscribeOn(Schedulers.computation())
                    .doOnSuccess(transferUtility -> this.transferUtility = transferUtility);
        }

        return Maybe.just(transferUtility);
    }

    private Maybe<TransferUtility.Builder> createTransferUtilityBuilder(final Context context) {
        return awsMobileClientRepository.awsMobileClientMaybe(context)
                .retry()
                .map(awsMobileClient ->
                        TransferUtility.builder()
                                .context(context)
                                .awsConfiguration(new AWSConfiguration(context))
                                .s3Client(new AmazonS3Client(awsMobileClient.getCredentialsProvider()))
                );
    }

    private String getAbsoluteS3Path(final Context context, String key) {
        return getBucketUrl(context) + key;
    }

    private String getBucketUrl(final Context context) {
        JSONObject jsonObject = JsonUtil.jsonObjectFromFile(context, R.raw.awsconfiguration);
        String bucket = "";
        String region = "";
        try {
            JSONObject s3TransferUtility = jsonObject.getJSONObject("S3TransferUtility").getJSONObject("Default");
            region = s3TransferUtility.getString("Region");
            bucket = s3TransferUtility.getString("Bucket");
            return String.format("https://s3.%s.amazonaws.com/%s/", region, bucket);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Maybe<String> uploadFile(final Context context, final String s3Path, final Uri target) {
        return Maybe.create(e ->
                transferUtilityMaybe(context)
                        .doOnSuccess(transferUtility -> {
                            String scopedPath = SCOPE + s3Path;
                            TransferObserver uploadObserver = transferUtility.upload(scopedPath, new File(target.getPath()));
                            Log.d(TAG, "uploadFile: scopedPath=" + scopedPath + " target=" + target.getPath());

                            uploadObserver.setTransferListener(new TransferListener() {
                                @Override
                                public void onStateChanged(int id, TransferState state) {
                                    if (TransferState.COMPLETED == state) {
                                        String absoluteS3Path = getAbsoluteS3Path(context, scopedPath);
                                        Log.d(TAG, "onStateChanged: upload complete s3Path=" + absoluteS3Path);
                                        e.onSuccess(absoluteS3Path);
                                        e.onComplete();
                                    }
                                }

                                @Override
                                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                                    int percentDone = (int) percentDonef;

                                    Log.d(TAG, "ID:" + id +
                                            " bytesCurrent: " + bytesCurrent +
                                            " bytesTotal: " + bytesTotal +
                                            " " + percentDone + "%");
                                }

                                @Override
                                public void onError(int id, Exception ex) {
                                    // Handle errors
                                    e.onError(ex);
                                    e.onComplete();
                                }

                            });
                        }).subscribe());
    }
}
