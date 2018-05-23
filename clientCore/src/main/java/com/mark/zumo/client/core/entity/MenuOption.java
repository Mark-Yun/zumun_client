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

    @PrimaryKey @NonNull @SerializedName("menu_option_uuid") @ColumnInfo(name = "menu_option_uuid")
    public final String uuid;
    @SerializedName("menu_uuid") @ColumnInfo(name = "menu_uuid")
    public final String menuUuid;
    @SerializedName("option_name") @ColumnInfo(name = "option_name")
    public final String name;
    @SerializedName("option_value") @ColumnInfo(name = "option_value")
    public final String value;
    @SerializedName("option_price") @ColumnInfo(name = "option_price")
    public final int price;

    public MenuOption(@NonNull final String uuid, final String menuUuid, final String name, final String value, final int price) {
        this.uuid = uuid;
        this.menuUuid = menuUuid;
        this.name = name;
        this.value = value;
        this.price = price;
    }
}
