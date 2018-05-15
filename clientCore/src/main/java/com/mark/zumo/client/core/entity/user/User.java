package com.mark.zumo.client.core.entity.user;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.mark.zumo.client.core.dao.GuestUserDao;
import com.mark.zumo.client.core.entity.EntityHelper;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = GuestUserDao.TABLE_NAME)
public class User {
    @PrimaryKey public final long id;

    public User(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }
}
