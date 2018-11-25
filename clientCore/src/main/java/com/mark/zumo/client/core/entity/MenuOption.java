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
    public final String menuOptionCategoryUuid;
    @SerializedName(Schema.name) @ColumnInfo(name = Schema.name)
    public final String name;
    @SerializedName(Schema.value) @ColumnInfo(name = Schema.value)
    public final String value;
    @SerializedName(Schema.price) @ColumnInfo(name = Schema.price)
    public final int price;

    public MenuOption(@NonNull final String uuid, final String storeUuid, final String menuOptionCategoryUuid, final String name, final String value, final int price) {
        this.uuid = uuid;
        this.storeUuid = storeUuid;
        this.menuOptionCategoryUuid = menuOptionCategoryUuid;
        this.name = name;
        this.value = value;
        this.price = price;
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
    }
}
