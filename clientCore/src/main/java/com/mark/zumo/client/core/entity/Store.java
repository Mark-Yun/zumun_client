package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.mark.zumo.client.core.util.EntityHelper;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity
public class Store {

    @PrimaryKey public final long id;
    public final String name;
    public final long storeOwnerId;
    public final long createdDate;

    public Store(long id, String name, long storeOwnerId, long createdDate) {
        this.id = id;
        this.name = name;
        this.storeOwnerId = storeOwnerId;
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }
}
