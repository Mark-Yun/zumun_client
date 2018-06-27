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

import static com.mark.zumo.client.core.entity.MenuCategory.Schema.table;

/**
 * Created by mark on 18. 6. 27.
 */

@Entity(tableName = table)
public class MenuCategory {

    @PrimaryKey @NonNull @ColumnInfo(name = Schema.uuid) @SerializedName(Schema.uuid)
    public final String uuid;
    @ColumnInfo(name = Schema.name) @SerializedName(Schema.name)
    public final String name;
    @ColumnInfo(name = Schema.storeUuid) @SerializedName(Schema.storeUuid)
    public final String storeUuid;
    @ColumnInfo(name = Schema.seqNum) @SerializedName(Schema.seqNum)
    public final int seqNum;

    public MenuCategory(@NonNull final String uuid, final String name, final String storeUuid, final int seqNum) {
        this.uuid = uuid;
        this.name = name;
        this.storeUuid = storeUuid;
        this.seqNum = seqNum;
    }

    public interface Schema {
        String table = "menu_category";
        String uuid = "menu_category_uuid";
        String name = "menu_category_name";
        String storeUuid = "store_uuid";
        String seqNum = "menu_category_seq_num";
    }
}
