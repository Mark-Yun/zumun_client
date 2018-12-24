/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

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
    EMPTY_EMAIL(StoreUserSignupErrorCode.EMPTY_EMAIL, request -> !TextUtils.isEmpty(request.email)),
    EMPTY_PASSWORD(StoreUserSignupErrorCode.EMPTY_PASSWORD, request -> !TextUtils.isEmpty(request.password)),
    EMPTY_PASSWORD_CONFIRM(StoreUserSignupErrorCode.EMPTY_PASSWORD_CONFIRM, request -> !TextUtils.isEmpty(request.passwordConfirm)),
    PASSWORD_DISCORD(StoreUserSignupErrorCode.PASSWORD_DISCORD, request -> TextUtils.equals(request.password, request.passwordConfirm)),
    EMAIL_INCORRECT(StoreUserSignupErrorCode.EMAIL_INCORRECT, request -> request.email.matches(Validator.emailPattern));

    private final static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final AssertConsumer assertConsumer;
    private StoreUserSignupErrorCode storeUserSignupErrorCode;

    Validator(StoreUserSignupErrorCode storeUserSignupErrorCode, AssertConsumer assertConsumer) {
        this.storeUserSignupErrorCode = storeUserSignupErrorCode;
        this.assertConsumer = assertConsumer;
    }

    boolean verify(StoreOwnerSignUpRequest request) {
        return this.assertConsumer.assertConsumer(request);
    }

    StoreUserSignupErrorCode ofErrorCode() {
        return storeUserSignupErrorCode;
    }

    @FunctionalInterface
    private interface AssertConsumer {
        boolean assertConsumer(StoreOwnerSignUpRequest request);
    }
}
