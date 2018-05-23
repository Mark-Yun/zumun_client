package com.mark.zumo.client.core.entity.session;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.mark.zumo.client.core.dao.CustomerUserSessionDao;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = CustomerUserSessionDao.TABLE_NAME)
public class CustomerUserSession {

    @PrimaryKey public final long id;
    public final long customerUserId;
    public final long createdDate;
    public final long expiredDate;

    public CustomerUserSession(long id, long customerUserId, long createdDate, long expiredDate) {
        this.id = id;
        this.customerUserId = customerUserId;
        this.createdDate = createdDate;
        this.expiredDate = expiredDate;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }
}
