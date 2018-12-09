/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.mark.zumo.client.core.p2p.packet.CombinedResult;
import com.mark.zumo.client.core.util.JsonUtil;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.store.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 7. 15.
 */
public enum S3TransferManager {
    INSTANCE;

    private static final String TAG = "S3TransferManager";

    private Context context;
    private volatile AWSMobileClient awsMobileClient;

    S3TransferManager() {
        context = ContextHolder.getContext();
    }

    private static String getThumbnailImageDirPath(String sessionId) {
        return "public/store/" + sessionId + "/thumbnail_image/" + UUID.randomUUID() + ".jpg";
    }

    private static String getCoverImageDirPath(String sessionId) {
        return "public/store/" + sessionId + "/cover_image/" + UUID.randomUUID() + ".jpg";
    }

    private static String getMenuImageDirPath(String sessionId) {
        return "public/store/" + sessionId + "/menu_image/" + UUID.randomUUID() + ".jpg";
    }

    private static String getBankAccountScanDirPath() {
        return "public/store/bank_scan_url/" + UUID.randomUUID() + ".jpg";
    }

    private Maybe<AWSMobileClient> awsMobileClient(Activity activity) {
        return Maybe.create(e -> {
            if (awsMobileClient != null) {
                e.onSuccess(awsMobileClient);
            } else {
                AWSMobileClient.getInstance().initialize(activity, awsStartupResult -> {
                    Log.d(TAG, "AWSMobileClient is instantiated and you are connected to AWS!");
                    e.onSuccess(awsMobileClient = AWSMobileClient.getInstance());
                }).execute();
            }
        });
    }

    private TransferUtility transferUtility(final AmazonS3Client amazonS3Client,
                                            final AWSConfiguration awsConfiguration) {
        return TransferUtility.builder()
                .context(context)
                .awsConfiguration(awsConfiguration)
                .s3Client(amazonS3Client)
                .build();
    }

    private CombinedResult<AmazonS3Client, AWSConfiguration> prepareTransfer(AWSMobileClient awsMobileClient) {
        return new CombinedResult<>(
                new AmazonS3Client(awsMobileClient.getCredentialsProvider()),
                awsMobileClient.getConfiguration()
        );
    }

    private String getAbsoluteS3Path(String key) {
        return getBucketUrl() + key;
    }

    private String getBucketUrl() {
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

    private Maybe<String> uploadFile(Activity activity, String s3Path, Uri target) {
        return Maybe.create(e ->
                awsMobileClient(activity)
                        .map(this::prepareTransfer)
                        .map((combinedResult) -> transferUtility(combinedResult.t, combinedResult.r))
                        .doOnSuccess(transferUtility -> {
                            TransferObserver uploadObserver = transferUtility.upload(s3Path, new File(target.getPath()));
                            Log.d(TAG, "uploadFile: s3Path-" + s3Path + " target-" + target.getPath());

                            uploadObserver.setTransferListener(new TransferListener() {
                                @Override
                                public void onStateChanged(int id, TransferState state) {
                                    if (TransferState.COMPLETED == state) {
                                        String absoluteS3Path = getAbsoluteS3Path(s3Path);
                                        Log.d(TAG, "onStateChanged: upload complete s3Path-" + absoluteS3Path);
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

    public Maybe<String> uploadThumbnailImage(Activity activity, String storeUuid, Uri target) {
        return Maybe.fromCallable(() -> getThumbnailImageDirPath(storeUuid))
                .flatMap(s3Path -> uploadFile(activity, s3Path, target));
    }

    public Maybe<String> uploadCoverImage(Activity activity, String storeUuid, Uri target) {
        return Maybe.fromCallable(() -> getCoverImageDirPath(storeUuid))
                .flatMap(s3Path -> uploadFile(activity, s3Path, target));
    }

    public Maybe<String> uploadMenuImage(Activity activity, String menuUuid, Uri target) {
        return Maybe.fromCallable(() -> getMenuImageDirPath(menuUuid))
                .flatMap(s3Path -> uploadFile(activity, s3Path, target));
    }

    public Maybe<String> uploadBankScanImage(Activity activity, Uri target) {
        return Maybe.fromCallable(S3TransferManager::getBankAccountScanDirPath)
                .flatMap(s3Path -> uploadFile(activity, s3Path, target));
    }
}
