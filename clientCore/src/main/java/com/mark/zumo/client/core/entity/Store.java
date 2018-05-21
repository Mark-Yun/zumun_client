package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.dao.Converters;
import com.mark.zumo.client.core.dao.StoreDao;

import java.util.UUID;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = StoreDao.TABLE_NAME)
public class Store {

    @PrimaryKey @NonNull public final UUID uuid;
    public final String name;
    public final long latitude;
    public final long longitude;

    public Store(final UUID uuid, final String name, final long latitude, final long longitude) {
        this.uuid = uuid;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Store(final String uuidAsString, final String name, final long latitude, final long longitude) {
        this(Converters.fromBinary(uuidAsString.getBytes()), name, latitude, longitude);
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }
}
