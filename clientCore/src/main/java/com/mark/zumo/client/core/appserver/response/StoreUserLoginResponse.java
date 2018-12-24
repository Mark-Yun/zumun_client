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

package com.mark.zumo.client.core.appserver.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 18. 12. 23.
 */
public class StoreUserLoginResponse {
    @SerializedName(Schema.sessionToken)
    public final String sessionToken;

    public StoreUserLoginResponse(final String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public interface Schema {
        String sessionToken = "store_user_session_token";
    }
}
