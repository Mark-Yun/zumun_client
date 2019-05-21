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

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = Menu.MENU_TABLE)
public class Menu implements Serializable {
    public static final String MENU_TABLE = "menu";

    @PrimaryKey @NonNull @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.name) @ColumnInfo(name = Schema.name)
    public final String name;
    @SerializedName(Schema.storeUuid) @ColumnInfo(name = Schema.storeUuid)
    public final String storeUuid;
    @SerializedName(Schema.price) @ColumnInfo(name = Schema.price)
    public final int price;
    @SerializedName(Schema.imageUrl) @ColumnInfo(name = Schema.imageUrl)
    public final String imageUrl;

    public Menu(@NonNull final String uuid, final String name,
                final String storeUuid, final int price, final String imageUrl) {

        this.uuid = uuid;
        this.name = name;
        this.storeUuid = storeUuid;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public static Menu emptyMenu() {
        return new Menu("", "", "", -1, "");
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
        private String name;
        private String storeUuid;
        private int price;
        private String imageUrl;

        public Builder() {
        }

        public Builder(Menu menu) {
            this.uuid = menu.uuid;
            this.name = menu.name;
            this.storeUuid = menu.storeUuid;
            this.price = menu.price;
            this.imageUrl = menu.imageUrl;
        }

        public Builder setUuid(final String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setStoreUuid(final String storeUuid) {
            this.storeUuid = storeUuid;
            return this;
        }

        public Builder setPrice(final int price) {
            this.price = price;
            return this;
        }

        public Builder setImageUrl(final String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Menu build() {
            return new Menu(uuid, name, storeUuid, price, imageUrl);
        }
    }

    public interface Schema {
        String uuid = "menu_uuid";
        String name = "menu_name";
        String storeUuid = "store_uuid";
        String price = "menu_price";
        String imageUrl = "image_url";
    }
}
