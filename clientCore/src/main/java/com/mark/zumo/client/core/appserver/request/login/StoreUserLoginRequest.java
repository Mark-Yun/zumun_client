/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 18. 12. 23.
 */
public class StoreUserLoginRequest {
    @SerializedName(Schema.email)
    public final String email;

    @SerializedName(Schema.password)
    public final String password;

    public StoreUserLoginRequest(final String email, final String password) {
        this.email = email;
        this.password = password;
    }

    public interface Schema {
        String email = "store_user_email";
        String password = "store_user_password";
    }

    public static class Builder {
        private String email;
        private String password;

        public Builder setEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(final String password) {
            this.password = password;
            return this;
        }

        public StoreUserLoginRequest build() {
            return new StoreUserLoginRequest(email, password);
        }
    }
}
