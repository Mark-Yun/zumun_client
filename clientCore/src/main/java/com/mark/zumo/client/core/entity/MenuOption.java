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

import static com.mark.zumo.client.core.entity.MenuOption.Schema.table;

/**
 * Created by mark on 18. 5. 23.
 */

@Entity(tableName = table)
public class MenuOption implements Serializable {

    @PrimaryKey @NonNull @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.storeUuid) @ColumnInfo(name = Schema.storeUuid)
    public final String storeUuid;
    @SerializedName(Schema.menuOptionCategoryUuid) @ColumnInfo(name = Schema.menuOptionCategoryUuid)
    public String menuOptionCategoryUuid;
    @SerializedName(Schema.name) @ColumnInfo(name = Schema.name)
    public String name;
    @SerializedName(Schema.value) @ColumnInfo(name = Schema.value)
    public String value;
    @SerializedName(Schema.price) @ColumnInfo(name = Schema.price)
    public int price;
    @SerializedName(Schema.seqNum) @ColumnInfo(name = Schema.seqNum)
    public int seqNum;

    public MenuOption(@NonNull final String uuid, final String storeUuid, final String menuOptionCategoryUuid, final String name, final String value, final int price, final int seqNum) {
        this.uuid = uuid;
        this.storeUuid = storeUuid;
        this.menuOptionCategoryUuid = menuOptionCategoryUuid;
        this.name = name;
        this.value = value;
        this.price = price;
        this.seqNum = seqNum;
    }

    public static MenuOption create(String value, int price) {
        return new MenuOption("", "", "", "", value, price, 0);
    }

    public static MenuOption createEmptyMenuOption() {
        return new MenuOption("", "", "", "", "", -1, -1);
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
        String table = "menu_option";
        String uuid = "menu_option_uuid";
        String storeUuid = "store_uuid";
        String menuOptionCategoryUuid = "menu_option_category_uuid";
        String name = "menu_option_name";
        String value = "menu_option_value";
        String price = "menu_option_price";
        String seqNum = "menu_option_value_seq_num";
    }

    public static class Builder {
        private String uuid = "";
        private String storeUuid = "";
        private String menuOptionCategoryUuid = "";
        private String name = "";
        private String value = "";
        private int price = 0;
        private int seqNum = 0;

        public static Builder from(MenuOption menuOption) {
            Builder builder = new Builder();
            builder.uuid = menuOption.uuid;
            builder.storeUuid = menuOption.storeUuid;
            builder.menuOptionCategoryUuid = menuOption.menuOptionCategoryUuid;
            builder.name = menuOption.name;
            builder.value = menuOption.value;
            builder.price = menuOption.price;
            builder.seqNum = menuOption.seqNum;
            return builder;
        }

        public String getUuid() {
            return uuid;
        }

        public Builder setUuid(final String uuid) {
            this.uuid = uuid;
            return this;
        }

        public String getStoreUuid() {
            return storeUuid;
        }

        public Builder setStoreUuid(final String storeUuid) {
            this.storeUuid = storeUuid;
            return this;
        }

        public String getMenuOptionCategoryUuid() {
            return menuOptionCategoryUuid;
        }

        public Builder setMenuOptionCategoryUuid(final String menuOptionCategoryUuid) {
            this.menuOptionCategoryUuid = menuOptionCategoryUuid;
            return this;
        }

        public String getName() {
            return name;
        }

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public String getValue() {
            return value;
        }

        public Builder setValue(final String value) {
            this.value = value;
            return this;
        }

        public int getPrice() {
            return price;
        }

        public Builder setPrice(final int price) {
            this.price = price;
            return this;
        }

        public int getSeqNum() {
            return seqNum;
        }

        public Builder setSeqNum(final int seqNum) {
            this.seqNum = seqNum;
            return this;
        }

        public MenuOption build() {
            return new MenuOption(uuid, storeUuid, menuOptionCategoryUuid, name, value, price, seqNum);
        }
    }
}
