/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.bank;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 19. 1. 13.
 */
public class DepositRequest {

    @SerializedName(Schema.storeUserUuid)
    public final String storeUserUuid;
    @SerializedName(Schema.transferAmount)
    public final int transferAmount;

    public DepositRequest(final String storeUserUuid, final int transferAmount) {
        this.storeUserUuid = storeUserUuid;
        this.transferAmount = transferAmount;
    }

    public interface Schema {
        String storeUserUuid = "store_user_uuid";
        String transferAmount = "tran_amt";
    }
}
