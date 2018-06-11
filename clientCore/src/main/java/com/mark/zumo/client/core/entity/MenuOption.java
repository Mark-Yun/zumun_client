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

import static com.mark.zumo.client.core.entity.MenuOption.TABLE;

/**
 * Created by mark on 18. 5. 23.
 */

@Entity(tableName = TABLE)
public class MenuOption implements Serializable {
    public static final String TABLE = "menu_option";

    @PrimaryKey @NonNull @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.menuUuid) @ColumnInfo(name = Schema.menuUuid)
    public final String menuUuid;
    @SerializedName(Schema.name) @ColumnInfo(name = Schema.name)
    public final String name;
    @SerializedName(Schema.value) @ColumnInfo(name = Schema.value)
    public final String value;
    @SerializedName(Schema.price) @ColumnInfo(name = Schema.price)
    public final int price;

    public MenuOption(@NonNull final String uuid, final String menuUuid, final String name, final String value, final int price) {
        this.uuid = uuid;
        this.menuUuid = menuUuid;
        this.name = name;
        this.value = value;
        this.price = price;
    }

    public interface Schema {
        String uuid = "menu_option_uuid";
        String menuUuid = "menu_uuid";
        String name = "option_name";
        String value = "option_value";
        String price = "option_price";
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, MenuOption.class);
    }
}
