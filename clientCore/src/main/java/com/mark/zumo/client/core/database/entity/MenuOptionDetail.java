/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.core.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.database.entity.util.EntityComparator;
import com.mark.zumo.client.core.database.entity.util.EntityHelper;

import java.io.Serializable;

import static com.mark.zumo.client.core.database.entity.MenuOptionDetail.Schema.table;

/**
 * Created by mark on 18. 5. 23.
 */

@Entity(tableName = table)
public class MenuOptionDetail implements Serializable {

    @PrimaryKey @NonNull @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.menuUuid) @ColumnInfo(name = Schema.menuUuid)
    public final String menuUuid;
    @SerializedName(Schema.menuOptionCategoryUuid) @ColumnInfo(name = Schema.menuOptionCategoryUuid)
    public final String menuOptionCategoryUuid;
    @SerializedName(Schema.storeUuid) @ColumnInfo(name = Schema.storeUuid)
    public final String storeUuid;
    @SerializedName(Schema.seqNum) @ColumnInfo(name = Schema.seqNum)
    public final int seqNum;

    public MenuOptionDetail(@NonNull final String uuid, final String menuUuid, final String menuOptionCategoryUuid, final String storeUuid, final int seqNum) {
        this.uuid = uuid;
        this.menuUuid = menuUuid;
        this.menuOptionCategoryUuid = menuOptionCategoryUuid;
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

    public static class Builder {
        private String uuid;
        private String menuUuid;
        private String menuOptionCategoryUuid;
        private String storeUuid;
        private int seqNum;

        public Builder setUuid(final String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setMenuUuid(final String menuUuid) {
            this.menuUuid = menuUuid;
            return this;
        }

        public Builder setMenuOptionCategoryUuid(final String menuOptionCategoryUuid) {
            this.menuOptionCategoryUuid = menuOptionCategoryUuid;
            return this;
        }

        public Builder setStoreUuid(final String storeUuid) {
            this.storeUuid = storeUuid;
            return this;
        }

        public Builder setSeqNum(final int seqNum) {
            this.seqNum = seqNum;
            return this;
        }

        public MenuOptionDetail build() {
            return new MenuOptionDetail(uuid, menuUuid, menuOptionCategoryUuid, storeUuid, seqNum);
        }
    }
    public interface Schema {
        String table = "menu_option_detail";
        String uuid = "menu_option_detail_uuid";
        String menuOptionCategoryUuid = "menu_option_category_uuid";
        String menuUuid = "menu_uuid";
        String storeUuid = "store_uuid";
        String seqNum = "menu_option_seq_num";
    }
}
