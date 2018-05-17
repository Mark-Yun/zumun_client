package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.mark.zumo.client.core.dao.MenuItemDao;

import java.io.Serializable;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = MenuItemDao.TABLE_NAME)
public class MenuItem implements Serializable {

    private static final String TAG = "MenuItem";

    @PrimaryKey public final String uuid;
    public final String name;
    public final long storeId;
    public final int price;

    private MenuItem(final String uuid, final String name, final long storeId, final int price) {
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
