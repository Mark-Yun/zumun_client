/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 18. 12. 14.
 */
public class StoreUserHandShakeResponse {
    @SerializedName(Schema.publicKey)
    public final String publicKey;

    public StoreUserHandShakeResponse(final String publicKey) {
        this.publicKey = publicKey;
    }

    interface Schema {
        String publicKey = "public_key";
    }
}
