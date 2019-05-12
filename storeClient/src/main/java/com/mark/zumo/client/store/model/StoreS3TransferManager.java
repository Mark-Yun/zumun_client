/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.content.Context;
import android.net.Uri;

import com.mark.zumo.client.core.repository.S3BucketRepository;

import java.util.UUID;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 7. 15.
 */
public enum StoreS3TransferManager {
    INSTANCE;

    private static final String TAG = "StoreS3TransferManager";

    private final S3BucketRepository s3BucketRepository;

    StoreS3TransferManager() {
        s3BucketRepository = S3BucketRepository.INSTANCE;
    }

    private static String getThumbnailImageDirPath(String storeUuid) {
        return "store/" + storeUuid + "/thumbnail_image/" + UUID.randomUUID() + ".jpg";
    }

    private static String getCorporateScanImageDirPath(String storeUuid) {
        return "store/registration/" + storeUuid + "/corporate_scan_image/" + UUID.randomUUID() + ".jpg";
    }

    private static String getCoverImageDirPath(String storeUuid) {
        return "store/" + storeUuid + "/cover_image/" + UUID.randomUUID() + ".jpg";
    }

    private static String getMenuImageDirPath(String storeUuid) {
        return "store/" + storeUuid + "/menu_image/" + UUID.randomUUID() + ".jpg";
    }

    private static String getBankAccountScanDirPath() {
        return "store/bank_scan_url/" + UUID.randomUUID() + ".jpg";
    }

    private Maybe<String> uploadFile(Context context, String s3Path, Uri target) {
        return s3BucketRepository.uploadFile(context, s3Path, target);
    }

    public Maybe<String> uploadThumbnailImage(Context context, String storeUuid, Uri target) {
        return Maybe.fromCallable(() -> getThumbnailImageDirPath(storeUuid))
                .flatMap(s3Path -> uploadFile(context, s3Path, target))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<String> uploadCoverImage(Context context, String storeUuid, Uri target) {
        return Maybe.fromCallable(() -> getCoverImageDirPath(storeUuid))
                .flatMap(s3Path -> uploadFile(context, s3Path, target))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<String> uploadMenuImage(Context context, String menuUuid, Uri target) {
        return Maybe.fromCallable(() -> getMenuImageDirPath(menuUuid))
                .flatMap(s3Path -> uploadFile(context, s3Path, target))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<String> uploadBankScanImage(Context context, Uri target) {
        return Maybe.fromCallable(StoreS3TransferManager::getBankAccountScanDirPath)
                .flatMap(s3Path -> uploadFile(context, s3Path, target))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<String> uploadCorporateScanImage(Context context, String storeUserUuid, Uri target) {
        return Maybe.just(storeUserUuid)
                .map(StoreS3TransferManager::getCorporateScanImageDirPath)
                .flatMap(s3Path -> uploadFile(context, s3Path, target))
                .subscribeOn(Schedulers.io());
    }
}
