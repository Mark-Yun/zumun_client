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

/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.response.store.user.signup;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 19. 1. 18.
 */
public class StoreOwnerSignUpResponse {
    @SerializedName(Schema.storeUserSignUpResponse)
    public final String storeUserSignUpResponse;

    public StoreOwnerSignUpResponse(final String storeUserSignInResponse) {
        this.storeUserSignUpResponse = storeUserSignInResponse;
    }

    public interface Schema {
        String storeUserSignUpResponse = "store_user_sign_up_response";
    }
}
