package com.mark.zumo.client.core.entity.user;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.mark.zumo.client.core.entity.EntityHelper;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity
public class User {
    @PrimaryKey public final long id;
    public final String name;
    public final long createdDate;

    public User(long id, String name, long createdDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }
}
