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
import com.mark.zumo.client.core.entity.util.EntityComparator;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 12. 13.
 */

@Entity(tableName = StoreUser.Schema.TABLE)
public class StoreUser {

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
    public final String auth_type;
    @SerializedName(Schema.auth_token) @ColumnInfo(name = Schema.auth_token)
    public final String auth_token;

    public StoreUser(@NonNull final String uuid, final String name, final String password, final String phoneNumber, final String email, final String auth_type, final String auth_token) {
        this.uuid = uuid;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.auth_type = auth_type;
        this.auth_token = auth_token;
    }

    @Override
    public boolean equals(final Object obj) {
        return new EntityComparator<>().test(this, obj);
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    public interface Schema {
        String TABLE = "store_user";
        String uuid = "store_user_uuid";
        String password = "store_user_password";
        String name = "store_user_name";
        String phoneNumber = "store_user_phone_number";
        String email = "store_user_email";
        String auth_type = "authType";
        String auth_token = "authToken";
    }
}
