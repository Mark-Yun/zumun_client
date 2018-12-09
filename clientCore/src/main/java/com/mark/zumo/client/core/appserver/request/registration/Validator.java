/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.registration;

import android.text.TextUtils;

/**
 * Created by mark on 18. 11. 4.
 */
enum Validator {
    EMPTY_STORE_NAME(StoreRegistrationErrorCode.EMPTY_STORE_NAME, request -> !TextUtils.isEmpty(request.storeName)),
    EMPTY_STORE_PHONE_NUMBER(StoreRegistrationErrorCode.EMPTY_STORE_PHONE_NUMBER, request -> !TextUtils.isEmpty(request.storePhoneNumber)),
    EMPTY_STORE_TYPE(StoreRegistrationErrorCode.EMPTY_STORE_TYPE, request -> !TextUtils.isEmpty(request.storeType)),
    EMPTY_CORPORATE_REGISTRATION_NUMBER(StoreRegistrationErrorCode.EMPTY_CORPORATE_REGISTRATION_NUMBER, request -> !TextUtils.isEmpty(request.corporateRegistrationNumber)),
    EMPTY_CORPORATE_REGISTRATION_SCAN_URL(StoreRegistrationErrorCode.EMPTY_CORPORATE_REGISTRATION_SCAN_URL, request -> !TextUtils.isEmpty(request.corporateRegistrationScanUrl)),
    EMPTY_COVER_IMAGE_URL(StoreRegistrationErrorCode.EMPTY_COVER_IMAGE_URL, request -> !TextUtils.isEmpty(request.thumbnailImageUrl)),
    EMPTY_EMPTY_ADDRESS(StoreRegistrationErrorCode.EMPTY_EMPTY_ADDRESS, request -> !TextUtils.isEmpty(request.address)),
    EMPTY_EMPTY_LOCATION(StoreRegistrationErrorCode.EMPTY_EMPTY_LOCATION, request -> !(request.latitude < 0 || request.longitude < 0)),
    EMPTY_THUMBNAIL_IMAGE_URL(StoreRegistrationErrorCode.EMPTY_THUMBNAIL_IMAGE_URL, request -> !TextUtils.isEmpty(request.thumbnailImageUrl));

    private final AssertConsumer assertConsumer;
    private StoreRegistrationErrorCode storeRegistrationErrorCode;

    Validator(StoreRegistrationErrorCode storeRegistrationErrorCode, AssertConsumer assertConsumer) {
        this.storeRegistrationErrorCode = storeRegistrationErrorCode;
        this.assertConsumer = assertConsumer;
    }

    boolean verify(StoreRegistrationRequest request) {
        return this.assertConsumer.assertConsumer(request);
    }

    StoreRegistrationErrorCode ofErrorCode() {
        return storeRegistrationErrorCode;
    }

    @FunctionalInterface
    private interface AssertConsumer {
        boolean assertConsumer(StoreRegistrationRequest request);
    }
}
