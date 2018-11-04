/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.signup;

/**
 * Created by mark on 18. 11. 4.
 */
public class Exception extends Throwable {
    public final ErrorCode errorCode;
    public final String message;

    Exception(final ErrorCode errorCode) {
        super(errorCode.message);

        this.errorCode = errorCode;
        this.message = errorCode.message;
    }
}
