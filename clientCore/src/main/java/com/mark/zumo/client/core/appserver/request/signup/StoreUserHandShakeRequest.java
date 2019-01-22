/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.signup;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 18. 12. 14.
 */
public class StoreUserHandShakeRequest {

    @SerializedName(Schema.email)
    public final String email;

    public StoreUserHandShakeRequest(final String email) {
        this.email = email;
    }

    public interface Schema {
        String email = "store_user_email";
    }
}
