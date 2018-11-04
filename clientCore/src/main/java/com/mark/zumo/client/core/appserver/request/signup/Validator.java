/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.signup;

import android.text.TextUtils;

/**
 * Created by mark on 18. 11. 4.
 */
enum Validator {
    EMPTY_EMAIL(ErrorCode.EMPTY_EMAIL, request -> !TextUtils.isEmpty(request.email)),
    EMPTY_PASSWORD(ErrorCode.EMPTY_PASSWORD, request -> !TextUtils.isEmpty(request.password)),
    EMPTY_PASSWORD_CONFIRM(ErrorCode.EMPTY_PASSWORD_CONFIRM, request -> !TextUtils.isEmpty(request.passwordConfirm)),
    EMPTY_NAME(ErrorCode.EMPTY_NAME, request -> !TextUtils.isEmpty(request.name)),
    EMPTY_PHONE_NUMBER(ErrorCode.EMPTY_PHONE_NUMBER, request -> !TextUtils.isEmpty(request.phoneNumber)),
    EMPTY_BANK_NAME(ErrorCode.EMPTY_BANK_NAME, request -> !TextUtils.isEmpty(request.bankName)),
    EMPTY_BANK_ACCOUNT(ErrorCode.EMPTY_BANK_ACCOUNT, request -> !TextUtils.isEmpty(request.bankAccount)),
    EMPTY_BANK_ACCOUNT_URL(ErrorCode.EMPTY_BANK_ACCOUNT_URL, request -> !TextUtils.isEmpty(request.bankAccountScanUrl)),
    PASSWORD_DISCORD(ErrorCode.PASSWORD_DISCORD, request -> !request.password.equals(request.passwordConfirm)),
    EMAIL_INCORRECT(ErrorCode.EMAIL_INCORRECT, request -> !request.email.matches(Validator.emailPattern));

    private final static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final AssertConsumer assertConsumer;
    private ErrorCode errorCode;

    Validator(ErrorCode errorCode, AssertConsumer assertConsumer) {
        this.errorCode = errorCode;
        this.assertConsumer = assertConsumer;
    }

    boolean verify(StoreOwnerSignUpRequest request) {
        return this.assertConsumer.assertConsumer(request);
    }

    ErrorCode ofErrorCode() {
        return errorCode;
    }

    @FunctionalInterface
    private interface AssertConsumer {
        boolean assertConsumer(StoreOwnerSignUpRequest request);
    }
}
