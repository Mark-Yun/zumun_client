/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.core.appserver.request;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 9. 9.
 */
public class StoreOwnerSignUpRequest {

    @SerializedName(Schema.id)
    public final long id;
    @SerializedName(Schema.name)
    public final String name;
    @SerializedName(Schema.phoneNumber)
    public final double phoneNumber;
    @SerializedName(Schema.email)
    public final double email;
    @SerializedName(Schema.bankName)
    public final String bankName;
    @SerializedName(Schema.bankAccount)
    public final String bankAccount;
    @SerializedName(Schema.authType)
    public final String authType;
    @SerializedName(Schema.bankAccountScanUrl)
    public final String bankAccountScanUrl;
    @SerializedName(Schema.authToken)
    public final String authToken;

    public StoreOwnerSignUpRequest(final long id, final String name, final double phoneNumber, final double email, final String bankName, final String bankAccount, final String authType, final String bankAccountScanUrl, final String authToken) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.bankName = bankName;
        this.bankAccount = bankAccount;
        this.authType = authType;
        this.bankAccountScanUrl = bankAccountScanUrl;
        this.authToken = authToken;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    public interface Schema {
        String id = "id";
        String name = "store_owner_name";
        String phoneNumber = "store_owner_phone_number";
        String email = "store_owner_email";
        String bankName = "bank_name";
        String bankAccount = "bank_account";
        String bankAccountScanUrl = "bank_account_scan_url";
        String authType = "auth_type";
        String authToken = "auth_token";
    }
}
