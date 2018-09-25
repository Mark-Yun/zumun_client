/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 6. 15.
 */
@Entity(tableName = SnsToken.Schema.table)
public class SnsToken {
    @PrimaryKey @NonNull @ColumnInfo(name = Schema.uuid) @SerializedName(Schema.uuid)
    public final String uuid;
    @ColumnInfo(name = Schema.tokenType) @SerializedName(Schema.tokenType)
    public final String tokenType;
    @ColumnInfo(name = Schema.tokenValue) @SerializedName(Schema.tokenValue)
    public final String tokenValue;
    @ColumnInfo(name = Schema.createdDate) @SerializedName(Schema.createdDate)
    public long createdDate;

    public SnsToken(@NonNull final String uuid, final String tokenType, final String tokenValue) {
        this.uuid = uuid;
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
        this.createdDate = SystemClock.currentThreadTimeMillis();
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    public interface TokenType {
        String ANDROID = "android";
    }

    public interface Schema {
        String table = "sns_token";
        String uuid = "uuid";
        String tokenValue = "token_value";
        String tokenType = "token_type";
        String createdDate = "created_date";
    }
}
