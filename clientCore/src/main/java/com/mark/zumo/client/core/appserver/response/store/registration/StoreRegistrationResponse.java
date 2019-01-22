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

package com.mark.zumo.client.core.appserver.response.store.registration;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 19. 1. 18.
 */
public class StoreRegistrationResponse {

    @SerializedName(Schema.storeRegistrationResponse)
    public final String storeRegistrationResponse;

    public StoreRegistrationResponse(final String storeUserSignInResponse) {
        this.storeRegistrationResponse = storeUserSignInResponse;
    }

    public interface Schema {
        String storeRegistrationResponse = "store_registration_response";
    }
}
