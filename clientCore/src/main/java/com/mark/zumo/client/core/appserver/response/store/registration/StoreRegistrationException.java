/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.core.appserver.response.store.registration;

/**
 * Created by mark on 18. 11. 4.
 */
public class StoreRegistrationException extends Throwable {
    public final StoreRegistrationErrorCode storeUserSignupErrorCode;
    public final String message;

    public StoreRegistrationException(final StoreRegistrationErrorCode storeUserSignupErrorCode) {
        super(storeUserSignupErrorCode.message);

        this.storeUserSignupErrorCode = storeUserSignupErrorCode;
        this.message = storeUserSignupErrorCode.message;
    }
}
