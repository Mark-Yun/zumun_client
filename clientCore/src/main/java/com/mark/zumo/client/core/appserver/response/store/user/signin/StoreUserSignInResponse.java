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

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.response.store.user.signin;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 18. 12. 23.
 */
public class StoreUserSignInResponse {
    @SerializedName(Schema.sessionToken)
    public final String sessionToken;
    @SerializedName(Schema.storeUserUuid)
    public final String storeUserUuid;
    @SerializedName(Schema.storeUserSignInResponse)
    public final String storeUserSignInResponse;

    public StoreUserSignInResponse(final String sessionToken, final String storeUserUuid, final String storeUserSignInResponse) {
        this.sessionToken = sessionToken;
        this.storeUserUuid = storeUserUuid;
        this.storeUserSignInResponse = storeUserSignInResponse;
    }

    public interface Schema {
        String sessionToken = "store_user_session_token";
        String storeUserUuid = "store_user_uuid";
        String storeUserSignInResponse = "store_user_sign_in_response";
    }
}
