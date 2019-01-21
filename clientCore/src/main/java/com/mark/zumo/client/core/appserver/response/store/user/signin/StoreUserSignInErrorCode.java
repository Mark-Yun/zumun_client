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

import com.mark.zumo.client.core.R;
import com.mark.zumo.client.core.util.context.ContextHolder;

/**
 * Created by mark on 19. 1. 18.
 */
public enum StoreUserSignInErrorCode {

    OK(R.string.sign_in_error_message),
    FAIL(R.string.sign_in_error_message);

    public final String message;

    StoreUserSignInErrorCode(final int messageRes) {
        this.message = ContextHolder.getContext().getString(messageRes);
    }
}
