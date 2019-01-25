/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.util;

import com.mark.zumo.client.core.appserver.request.signup.StoreOwnerSignUpRequest;
import com.mark.zumo.client.core.appserver.response.store.user.signup.StoreUserSignupException;

import java.util.UUID;

/**
 * Created by mark on 18. 5. 3.
 */

public class DebugUtil {

    private static final String TEST_STORE_UUID = "FD8BC4DD00B04E60A166A5FBD3454E8F";

    public static StoreOwnerSignUpRequest storeOwnerSignUpRequest() throws StoreUserSignupException {
        return new StoreOwnerSignUpRequest.Builder()
                .setEmail(UUID.randomUUID().toString().substring(0, 10) + "@email.com")
                .setPassword("12341234")
                .setPasswordConfirm("12341234")
                .setName("sisisi")
                .setPhoneNumber(UUID.randomUUID().toString().substring(0, 10))
                .setBankName("bank")
                .setBankAccount(UUID.randomUUID().toString().substring(0, 10))
                .setBankAccountScanUrl("backAccountUrl")
                .build();
    }
}
