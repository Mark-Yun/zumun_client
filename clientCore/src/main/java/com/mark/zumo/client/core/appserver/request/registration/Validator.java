/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.registration;

import android.text.TextUtils;

import com.mark.zumo.client.core.appserver.response.store.registration.StoreRegistrationErrorCode;

/**
 * Created by mark on 18. 11. 4.
 */
enum Validator {
    EMPTY_STORE_NAME(StoreRegistrationErrorCode.EMPTY_STORE_NAME, request -> !TextUtils.isEmpty(request.storeName)),
    EMPTY_STORE_PHONE_NUMBER(StoreRegistrationErrorCode.EMPTY_STORE_PHONE_NUMBER, request -> !TextUtils.isEmpty(request.storePhoneNumber)),
    EMPTY_STORE_TYPE(StoreRegistrationErrorCode.EMPTY_STORE_TYPE, request -> !TextUtils.isEmpty(request.storeType)),
    EMPTY_EMPTY_ADDRESS(StoreRegistrationErrorCode.EMPTY_EMPTY_ADDRESS, request -> !TextUtils.isEmpty(request.storeAddress)),
    EMPTY_CORPORATE_REGISTRATION_NAME(StoreRegistrationErrorCode.EMPTY_CORPORATE_REGISTRATION_NAME, request -> !TextUtils.isEmpty(request.corporateRegistrationName)),
    EMPTY_CORPORATE_REGISTRATION_OWNER_NAME(StoreRegistrationErrorCode.EMPTY_CORPORATE_REGISTRATION_OWNER_NAME, request -> !TextUtils.isEmpty(request.corporateRegistrationOwnerName)),
    EMPTY_CORPORATE_REGISTRATION_NUMBER(StoreRegistrationErrorCode.EMPTY_CORPORATE_REGISTRATION_NUMBER, request -> !TextUtils.isEmpty(request.corporateRegistrationNumber)),
    EMPTY_CORPORATE_REGISTRATION_ADDRESS(StoreRegistrationErrorCode.EMPTY_CORPORATE_REGISTRATION_ADDRESS, request -> !TextUtils.isEmpty(request.corporateRegistrationAddress)),
    EMPTY_CORPORATE_REGISTRATION_SCAN_URL(StoreRegistrationErrorCode.EMPTY_CORPORATE_REGISTRATION_SCAN_URL, request -> !TextUtils.isEmpty(request.corporateRegistrationScanUrl)),
    ;
    
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
