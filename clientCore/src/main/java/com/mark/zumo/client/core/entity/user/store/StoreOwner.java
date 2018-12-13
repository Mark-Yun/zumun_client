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

package com.mark.zumo.client.core.entity.user.store;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 12. 13.
 */

@Entity(tableName = StoreOwner.Schema.TABLE)
public class StoreOwner extends StoreUser {

    @SerializedName(Schema.bankName) @ColumnInfo(name = Schema.bankName)
    public final String bankName;
    @SerializedName(Schema.bankAccount) @ColumnInfo(name = Schema.bankAccount)
    public final String bankAccount;
    @SerializedName(Schema.bankAccountScanUrl) @ColumnInfo(name = Schema.bankAccountScanUrl)
    public final String bankAccountScanUrl;
    @SerializedName(Schema.requestId) @ColumnInfo(name = Schema.requestId)
    public final long requestId;
    @SerializedName(Schema.createdDate) @ColumnInfo(name = Schema.createdDate)
    public final long createdDate;
    @SerializedName(Schema.userStatus) @ColumnInfo(name = Schema.userStatus)
    public final String userStatus;
    @SerializedName(Schema.userStatus) @ColumnInfo(name = Schema.contractVersion)
    public final int contractVersion;

    public StoreOwner(@NonNull final String uuid, final String name, final String password, final String phoneNumber, final String email, final String auth_type, final String auth_token, final String bankName, final String bankAccount, final String bankAccountScanUrl, final long requestId, final long createdDate, final String userStatus, final int contractVersion) {
        super(uuid, name, password, phoneNumber, email, auth_type, auth_token);
        this.bankName = bankName;
        this.bankAccount = bankAccount;
        this.bankAccountScanUrl = bankAccountScanUrl;
        this.requestId = requestId;
        this.createdDate = createdDate;
        this.userStatus = userStatus;
        this.contractVersion = contractVersion;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    public interface Schema extends StoreUser.Schema {
        String TABLE = "store_owner";
        String bankName = "bank_name";
        String bankAccount = "bank_account";
        String bankAccountScanUrl = "bank_account_scan_url";
        String requestId = "request_id";
        String createdDate = "created_date";
        String userStatus = "user_status";
        String contractVersion = "contract_version";
    }
}
