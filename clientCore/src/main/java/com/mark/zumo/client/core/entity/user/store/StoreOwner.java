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
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 12. 13.
 */

@Entity(tableName = StoreOwner.Schema.TABLE)
public class StoreOwner {

    @PrimaryKey @NonNull @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.name) @ColumnInfo(name = Schema.name)
    public final String name;
    @SerializedName(Schema.password) @ColumnInfo(name = Schema.password)
    public final String password;
    @SerializedName(Schema.phoneNumber) @ColumnInfo(name = Schema.phoneNumber)
    public final String phoneNumber;
    @SerializedName(Schema.email) @ColumnInfo(name = Schema.email)
    public final String email;
    @SerializedName(Schema.auth_type) @ColumnInfo(name = Schema.auth_type)
    public final String authType;
    @SerializedName(Schema.auth_token) @ColumnInfo(name = Schema.auth_token)
    public final String authToken;
    @SerializedName(Schema.bankName) @ColumnInfo(name = Schema.bankName)
    public final String bankName;
    @SerializedName(Schema.bankAccountNumber) @ColumnInfo(name = Schema.bankAccountNumber)
    public final String bankAccountNumber;
    @SerializedName(Schema.bankAccountScanUrl) @ColumnInfo(name = Schema.bankAccountScanUrl)
    public final String bankAccountScanUrl;
    @SerializedName(Schema.requestId) @ColumnInfo(name = Schema.requestId)
    public final long requestId;
    @SerializedName(Schema.createdDate) @ColumnInfo(name = Schema.createdDate)
    public final long createdDate;
    @SerializedName(Schema.userStatus) @ColumnInfo(name = Schema.userStatus)
    public final String userStatus;
    @SerializedName(Schema.contractVersion) @ColumnInfo(name = Schema.contractVersion)
    public final int contractVersion;

    public StoreOwner(@NonNull final String uuid, final String name, final String password, final String phoneNumber, final String email, final String authType, final String authToken, final String bankName, final String bankAccountNumber, final String bankAccountScanUrl, final long requestId, final long createdDate, final String userStatus, final int contractVersion) {
        this.uuid = uuid;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.authType = authType;
        this.authToken = authToken;
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
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

    public enum Permission {
        OWNER
    }

    public interface Schema {
        String TABLE = "store_owner";

        String uuid = "store_user_uuid";
        String password = "store_user_password";
        String name = "store_user_name";
        String phoneNumber = "store_user_phone_number";
        String email = "store_user_email";
        String auth_type = "authType";
        String auth_token = "authToken";
        String bankName = "bank_name";
        String bankAccountNumber = "bank_account_number";
        String bankAccountScanUrl = "bank_account_scan_url";
        String requestId = "request_id";
        String createdDate = "created_date";
        String userStatus = "user_status";
        String contractVersion = "contract_version";
    }
}
