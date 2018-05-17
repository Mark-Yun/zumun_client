package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.dao.StoreDao;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = StoreDao.TABLE_NAME)
public class Store {

    @PrimaryKey @NonNull public final String uuid;
    public final String name;
    public final long latitude;
    public final long longitude;

    public Store(final String uuid, final String name, final long latitude, final long longitude) {
        this.uuid = uuid;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }
}
