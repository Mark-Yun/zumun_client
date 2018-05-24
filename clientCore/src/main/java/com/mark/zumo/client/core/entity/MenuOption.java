package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.dao.MenuOptionDao;

import java.io.Serializable;

/**
 * Created by mark on 18. 5. 23.
 */

@Entity(tableName = MenuOptionDao.TABLE_NAME)
public class MenuOption implements Serializable {

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

    private interface Schema {
        String uuid = "menu_option_uuid";
        String menuUuid = "menu_uuid";
        String name = "option_name";
        String value = "option_value";
        String price = "option_price";
    }
}
