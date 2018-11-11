/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


/**
 * Created by mark on 18. 11. 11.
 */
@Entity(tableName = StoreUserSession.Schema.tableName)
public class StoreUserSession {

    @PrimaryKey @NonNull @SerializedName(Schema.storeUserSessionUuid) @ColumnInfo(name = Schema.storeUserSessionUuid)
    public final String storeUserSessionUuid;
    @SerializedName(Schema.storeUserUuid) @ColumnInfo(name = Schema.storeUserUuid)
    public final String storeUserUuid;
    @SerializedName(Schema.storeUuid) @ColumnInfo(name = Schema.storeUuid)
    public final String storeUuid;
    @SerializedName(Schema.storePermission) @ColumnInfo(name = Schema.storePermission)
    public final int storePermission;

    public StoreUserSession(@NonNull final String storeUserSessionUuid, final String storeUserUuid, final String storeUuid, final int storePermission) {
        this.storeUserSessionUuid = storeUserSessionUuid;
        this.storeUserUuid = storeUserUuid;
        this.storeUuid = storeUuid;
        this.storePermission = storePermission;
    }

    interface Schema {
        String tableName = "store_user_session";
        String storeUserSessionUuid = "store_user_session_uuid";
        String storeUserUuid = "store_user_uuid";
        String storeUuid = "store_uuid";
        String storePermission = "store_permission";
    }
}
