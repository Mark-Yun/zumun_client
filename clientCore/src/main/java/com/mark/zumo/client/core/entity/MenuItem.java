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
    @PrimaryKey public final long id;
    public final byte[] image;
    public final long storeId;
    public final int price;
    public final long createdDate;
    public final long modifiedDate;

    public MenuItem(long id, byte[] image, long storeId, int price, long createdDate, long modifiedDate) {
        this.id = id;
        this.image = image;
        this.storeId = storeId;
        this.price = price;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }
}
