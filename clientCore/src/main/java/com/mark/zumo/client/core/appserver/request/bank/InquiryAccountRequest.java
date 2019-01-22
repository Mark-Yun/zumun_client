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
public class InquiryAccountRequest {

    @SerializedName(Schema.bankAccountNumber)
    public final String bankAccountNumber;
    @SerializedName(Schema.accountHolderInfo)
    public final String accountHolderInfo;
    @SerializedName(Schema.bankCode)
    public final String bankCode;

    public InquiryAccountRequest(final String bankAccountNumber, final String accountHolderInfo, final String bankCode) {
        this.bankAccountNumber = bankAccountNumber;
        this.accountHolderInfo = accountHolderInfo;
        this.bankCode = bankCode;
    }

    public interface Schema {
        String bankAccountNumber = "bank_account_number";
        String accountHolderInfo = "account_holder_info";
        String bankCode = "bank_code";
    }
}
