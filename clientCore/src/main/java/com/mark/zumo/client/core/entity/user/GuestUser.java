package com.mark.zumo.client.core.entity.user;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.dao.GuestUserDao;
import com.mark.zumo.client.core.entity.EntityHelper;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = GuestUserDao.TABLE_NAME)
public class GuestUser {

    @PrimaryKey @NonNull
    private String uuid;

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, GuestUser.class);
    }
}
