package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.dao.MenuDao;

import java.io.Serializable;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = MenuDao.TABLE_NAME)
public class Menu implements Serializable {
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

    public Menu(@NonNull final String uuid, final String name, final String storeUuid, final int price, final String imageUrl) {
        this.uuid = uuid;
        this.name = name;
        this.storeUuid = storeUuid;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }

    private interface Schema {
        String uuid = "menu_uuid";
        String name = "menu_name";
        String storeUuid = "store_uuid";
        String price = "menu_price";
        String imageUrl = "image_url";
    }
}
