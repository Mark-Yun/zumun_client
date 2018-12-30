/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.entity.user.store;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 12. 23.
 */
@Entity(tableName = StoreUserSession.Schema.table)
public class StoreUserSession {

    @PrimaryKey @NonNull @ColumnInfo(name = Schema.email)
    public final String email;
    @ColumnInfo(name = Schema.token)
    public final String token;
    @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @ColumnInfo(name = Schema.password)
    public final String password;
    @ColumnInfo(name = Schema.createdDate)
    public final long createdDate;
    @Ignore
    public final Result result;

    public StoreUserSession(@NonNull final String email, final String token, final String uuid, final String password, final long createdDate) {
        this.email = email;
        this.token = token;
        this.uuid = uuid;
        this.password = password;
        this.createdDate = createdDate;
        this.result = Result.SUCCESS;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    public enum Result {
        SUCCESS
    }

    public interface Schema {
        String table = "store_user_session";
        String email = "store_user_email";
        String uuid = "store_user_uuid";
        String token = "store_user_session_token";
        String password = "store_user_password";
        String createdDate = "created_date";
    }

    public static class Builder {
        private String email;
        private String uuid;
        private String token;
        private String password;
        private Result result;

        public Builder setEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder setToken(final String token) {
            this.token = token;
            return this;
        }

        public Builder setPassword(final String password) {
            this.password = password;
            return this;
        }

        public Builder setResult(final Result result) {
            this.result = result;
            return this;
        }

        public Builder setUuid(final String uuid) {
            this.uuid = uuid;
            return this;
        }

        public StoreUserSession build() {
            return new StoreUserSession(email, token, uuid, password, System.currentTimeMillis());
        }
    }
}
