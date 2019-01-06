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
package com.mark.zumo.client.core.appserver.request.signup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.user.store.StoreOwner;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 9. 9.
 */
public class StoreOwnerSignUpRequest {

    @SerializedName(Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.name)
    public final String name;
    @Expose
    public final String passwordConfirm;
    @SerializedName(Schema.bankAccountNumber)
    public final String bankAccountNumber;
    @SerializedName(Schema.phoneNumber)
    public final String phoneNumber;
    @SerializedName(Schema.email)
    public final String email;
    @SerializedName(Schema.bankName)
    public final String bankName;
    @SerializedName(Schema.storePermission)
    public final String storePermission;
    @SerializedName(Schema.authType)
    public final String authType;
    @SerializedName(Schema.bankAccountScanUrl)
    public final String bankAccountScanUrl;
    @SerializedName(Schema.authToken)
    public final String authToken;
    @SerializedName(Schema.password)
    public String password;

    private StoreOwnerSignUpRequest(final String uuid, final String name, final String password, final String passwordConfirm, final String phoneNumber, final String email, final String bankName, final String bankAccountNumber, final String authType, final String bankAccountScanUrl, final String authToken) {
        this.uuid = uuid;
        this.name = name;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
        this.authType = authType;
        this.bankAccountScanUrl = bankAccountScanUrl;
        this.authToken = authToken;
        this.storePermission = StoreOwner.Permission.OWNER.name();
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    public interface Schema {
        String uuid = "store_user_request_uuid";
        String password = "store_user_password";
        String name = "store_user_name";
        String phoneNumber = "store_user_phone_number";
        String email = "store_user_email";
        String bankName = "bank_name";
        String bankAccountNumber = "bank_account_number";
        String bankAccountScanUrl = "bank_account_scan_url";
        String authType = "auth_type";
        String authToken = "auth_token";
        String storePermission = "store_permission";
    }

    public static class Builder {

        private String uuid = "";
        private String name = "";
        private String password = "";
        private String passwordConfirm = "";
        private String phoneNumber = "";
        private String email = "";
        private String bankName = "";
        private String bankAccount = "";
        private String authType = "";
        private String bankAccountScanUrl = "";
        private String authToken = "";

        public Builder() {
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

        public Builder setPasswordConfirm(final String passwordConfirm) {
            this.passwordConfirm = passwordConfirm;
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

        public Builder setBankName(final String bankName) {
            this.bankName = bankName;
            return this;
        }

        public Builder setBankAccount(final String bankAccount) {
            this.bankAccount = bankAccount;
            return this;
        }

        public Builder setAuthType(final String authType) {
            this.authType = authType;
            return this;
        }

        public Builder setBankAccountScanUrl(final String bankAccountScanUrl) {
            this.bankAccountScanUrl = bankAccountScanUrl;
            return this;
        }

        public Builder setAuthToken(final String authToken) {
            this.authToken = authToken;
            return this;
        }

        public StoreOwnerSignUpRequest build() throws StoreUserSignupException {
            StoreOwnerSignUpRequest storeOwnerSignUpRequest = new StoreOwnerSignUpRequest(uuid,
                    name, password, passwordConfirm, phoneNumber, email, bankName, bankAccount,
                    authType, bankAccountScanUrl, authToken);

            for (Validator validator : Validator.values()) {
                if (!validator.verify(storeOwnerSignUpRequest)) {
                    throw new StoreUserSignupException(validator.ofErrorCode());
                }
            }

            return storeOwnerSignUpRequest;
        }
    }

}
