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
import com.mark.zumo.client.core.entity.util.EntityComparator;
import com.mark.zumo.client.core.entity.util.EntityHelper;

import java.io.Serializable;

/**
 * Created by mark on 18. 8. 5.
 */
@Entity(tableName = MenuDetail.Schema.TABLE)
public class MenuDetail implements Serializable {

    @PrimaryKey @NonNull @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public String uuid;
    @SerializedName(Schema.menuUuid) @ColumnInfo(name = Schema.menuUuid)
    public String menuUuid;
    @SerializedName(Schema.menuCategoryUuid) @ColumnInfo(name = Schema.menuCategoryUuid)
    public String menuCategoryUuid;
    @SerializedName(Schema.menuSeqNum) @ColumnInfo(name = Schema.menuSeqNum)
    public int menuSeqNum;
    @SerializedName(Schema.storeUuid) @ColumnInfo(name = Schema.storeUuid)
    public String storeUuid;

    public MenuDetail(@NonNull final String uuid, final String menuUuid, final String menuCategoryUuid, final int menuSeqNum, final String storeUuid) {
        this.uuid = uuid;
        this.menuUuid = menuUuid;
        this.menuCategoryUuid = menuCategoryUuid;
        this.menuSeqNum = menuSeqNum;
        this.storeUuid = storeUuid;
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
        String TABLE = "menu_detail";
        String uuid = "menu_detail_uuid";
        String menuUuid = "menu_uuid";
        String menuCategoryUuid = "menu_category_uuid";
        String menuSeqNum = "menu_seq_num";
        String storeUuid = "store_uuid";
    }

    public static class Builder {
        private String uuid;
        private String menuUuid;
        private String menuCategoryUuid;
        private int menuSeqNum;
        private String storeUuid;

        public Builder setId(final String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setMenuUuid(final String menuUuid) {
            this.menuUuid = menuUuid;
            return this;
        }

        public Builder setMenuCategoryUuid(final String menuCategoryUuid) {
            this.menuCategoryUuid = menuCategoryUuid;
            return this;
        }

        public Builder setMenuSeqNum(final int menuSeqNum) {
            this.menuSeqNum = menuSeqNum;
            return this;
        }

        public Builder setStoreUuid(final String storeUuid) {
            this.storeUuid = storeUuid;
            return this;
        }

        public MenuDetail build() {
            return new MenuDetail(uuid, menuUuid, menuCategoryUuid, menuSeqNum, storeUuid);
        }
    }
}
