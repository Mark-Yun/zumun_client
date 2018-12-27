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

/**
 * Created by mark on 18. 12. 26.
 */
@Entity(tableName = StoreUserContract.Schema.table)
public class StoreUserContract {

    @NonNull @PrimaryKey @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.storeUserUuid) @ColumnInfo(name = Schema.storeUserUuid)
    public final String storeUserUuid;
    @SerializedName(Schema.storeUuid) @ColumnInfo(name = Schema.storeUuid)
    public final String storeUuid;
    @SerializedName(Schema.permission) @ColumnInfo(name = Schema.permission)
    public final String permission;


    public StoreUserContract(@NonNull final String uuid, final String storeUserUuid, final String storeUuid, final String permission) {
        this.uuid = uuid;
        this.storeUserUuid = storeUserUuid;
        this.storeUuid = storeUuid;
        this.permission = permission;
    }

    public interface Schema {
        String table = "store_contract";
        String uuid = "store_contract_uuid";
        String storeUserUuid = "store_user_uuid";
        String storeUuid = "store_uuid";
        String permission = "permission";
        String createdDate = "created_date";
    }
}
