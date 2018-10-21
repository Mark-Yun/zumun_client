/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityComparator;
import com.mark.zumo.client.core.entity.util.EntityHelper;

import java.io.Serializable;

import static com.mark.zumo.client.core.entity.MenuOptionDetail.Schema.table;

/**
 * Created by mark on 18. 5. 23.
 */

@Entity(tableName = table)
public class MenuOptionDetail implements Serializable {

    @PrimaryKey @SerializedName(Schema.id) @ColumnInfo(name = Schema.id)
    public final long id;
    @SerializedName(Schema.menuUuid) @ColumnInfo(name = Schema.menuUuid)
    public final String menuUuid;
    @SerializedName(Schema.menu_option_uuid) @ColumnInfo(name = Schema.menu_option_uuid)
    public final String menu_option_uuid;
    @SerializedName(Schema.storeUuid) @ColumnInfo(name = Schema.storeUuid)
    public final String storeUuid;
    @SerializedName(Schema.seqNum) @ColumnInfo(name = Schema.seqNum)
    public final int seqNum;

    public MenuOptionDetail(final long id, final String menuUuid, final String menu_option_uuid, final String storeUuid, final int seqNum) {
        this.id = id;
        this.menuUuid = menuUuid;
        this.menu_option_uuid = menu_option_uuid;
        this.storeUuid = storeUuid;
        this.seqNum = seqNum;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return new EntityComparator<>().test(this, obj);
    }

    public interface Schema {
        String table = "menu_option_detail";
        String id = "id";
        String menuUuid = "menu_uuid";
        String menu_option_uuid = "menu_option_uuid";
        String storeUuid = "store_uuid";
        String seqNum = "menu_option_seq_num";
    }
}
