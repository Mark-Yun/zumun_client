/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.response.store.user.signin;

/**
 * Created by mark on 19. 1. 18.
 */
public class StoreUserSignInException extends Exception {
    public final StoreUserSignInErrorCode storeUserSignInErrorCode;
    public final String message;

    public StoreUserSignInException(final StoreUserSignInErrorCode storeUserSignInErrorCode) {
        super(storeUserSignInErrorCode.message);

        this.storeUserSignInErrorCode = storeUserSignInErrorCode;
        this.message = storeUserSignInErrorCode.message;
    }
}
