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
import com.mark.zumo.client.core.entity.util.EntityHelper;

import java.io.Serializable;

/**
 * Created by mark on 18. 8. 5.
 */
@Entity(tableName = MenuDetail.Schema.TABLE)
public class MenuDetail implements Serializable {

    @PrimaryKey @NonNull @SerializedName(Schema.id) @ColumnInfo(name = Schema.id)
    public long id;
    @SerializedName(Schema.menuUuid) @ColumnInfo(name = Schema.menuUuid)
    public String menuUuid;
    @SerializedName(Schema.menuCategoryUuid) @ColumnInfo(name = Schema.menuCategoryUuid)
    public String menuCategoryUuid;
    @SerializedName(Schema.menuSeqNum) @ColumnInfo(name = Schema.menuSeqNum)
    public int menuSeqNum;
    @SerializedName(Schema.storeUuid) @ColumnInfo(name = Schema.storeUuid)
    public String storeUuid;

    public MenuDetail(@NonNull final long id, final String menuUuid, final String menuCategoryUuid, final int menuSeqNum, final String storeUuid) {
        this.id = id;
        this.menuUuid = menuUuid;
        this.menuCategoryUuid = menuCategoryUuid;
        this.menuSeqNum = menuSeqNum;
        this.storeUuid = storeUuid;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }

    public interface Schema {
        String TABLE = "menu_detail";
        String id = "id";
        String menuUuid = "menu_uuid";
        String menuCategoryUuid = "menu_category_uuid";
        String menuSeqNum = "menu_seq_num";
        String storeUuid = "store_uuid";
    }
}
