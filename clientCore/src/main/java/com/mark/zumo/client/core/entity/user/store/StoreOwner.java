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
    @SerializedName(Schema.bankCode) @ColumnInfo(name = Schema.bankCode)
    public final String bankCode;
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

    public StoreOwner(@NonNull final String uuid, final String name, final String password, final String phoneNumber, final String email, final String authType, final String authToken, final String bankCode, final String bankAccountNumber, final String bankAccountScanUrl, final long requestId, final long createdDate, final String userStatus, final int contractVersion) {
        this.uuid = uuid;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.authType = authType;
        this.authToken = authToken;
        this.bankCode = bankCode;
        this.bankAccountNumber = bankAccountNumber;
        this.bankAccountScanUrl = bankAccountScanUrl;
        this.requestId = requestId;
        this.createdDate = createdDate;
        this.userStatus = userStatus;
        this.contractVersion = contractVersion;
    }

    public static class Builder {
        private String uuid = "";
        private String name = "";
        private String password = "";
        private String phoneNumber = "";
        private String email = "";
        private String authType = "";
        private String authToken = "";
        private String bankCode = "";
        private String bankAccountNumber = "";
        private String bankAccountScanUrl = "";
        private long requestId = 0;
        private long createdDate = 0;
        private String userStatus = "";
        private int contractVersion = 0;

        public static Builder from(StoreOwner storeOwner) {
            return new Builder()
                    .setUuid(storeOwner.uuid)
                    .setName(storeOwner.name)
                    .setPassword(storeOwner.password)
                    .setPhoneNumber(storeOwner.phoneNumber)
                    .setEmail(storeOwner.email)
                    .setAuthType(storeOwner.authType)
                    .setAuthToken(storeOwner.authToken)
                    .setBankCode(storeOwner.bankCode)
                    .setBankAccountNumber(storeOwner.bankAccountNumber)
                    .setBankAccountScanUrl(storeOwner.bankAccountScanUrl)
                    .setRequestId(storeOwner.requestId)
                    .setCreatedDate(storeOwner.createdDate)
                    .setUserStatus(storeOwner.userStatus)
                    .setContractVersion(storeOwner.contractVersion);
        }

        public Builder setUuid(final String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setPassword(final String password) {
            this.password = password;
            return this;
        }

        public Builder setPhoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder setAuthType(final String authType) {
            this.authType = authType;
            return this;
        }

        public Builder setAuthToken(final String authToken) {
            this.authToken = authToken;
            return this;
        }

        public Builder setBankCode(final String bankCode) {
            this.bankCode = bankCode;
            return this;
        }

        public Builder setBankAccountNumber(final String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
            return this;
        }

        public Builder setBankAccountScanUrl(final String bankAccountScanUrl) {
            this.bankAccountScanUrl = bankAccountScanUrl;
            return this;
        }

        public Builder setRequestId(final long requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder setCreatedDate(final long createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder setUserStatus(final String userStatus) {
            this.userStatus = userStatus;
            return this;
        }

        public Builder setContractVersion(final int contractVersion) {
            this.contractVersion = contractVersion;
            return this;
        }

        public StoreOwner build() {
            return new StoreOwner(uuid, name, password, phoneNumber, email, authType, authToken, bankCode, bankAccountNumber, bankAccountScanUrl, requestId, createdDate, userStatus, contractVersion);
        }
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
        String bankCode = "bank_code";
        String bankAccountNumber = "bank_account_number";
        String bankAccountScanUrl = "bank_account_scan_url";
        String requestId = "request_id";
        String createdDate = "created_date";
        String userStatus = "user_status";
        String contractVersion = "contract_version";
    }
}
