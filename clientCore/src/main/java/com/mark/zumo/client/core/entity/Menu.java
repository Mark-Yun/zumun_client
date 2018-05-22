package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.dao.MenuItemDao;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = MenuItemDao.TABLE_NAME)
public class Menu implements Serializable {

    private static final String TAG = "Menu";

    @PrimaryKey @NonNull public final UUID uuid;
    public final String name;
    public final String storeId;
    public final int price;

    public Menu(final UUID uuid, final String name, final String storeId, final int price) {
        this.uuid = uuid;
        this.name = name;
        this.storeId = storeId;
        this.price = price;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }
}