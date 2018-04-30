package com.mark.zumo.client.core.entity.session;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.mark.zumo.client.core.entity.EntityHelper;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity
public class StoreSession {

    @PrimaryKey public final long id;
    public final long storeId;
    public final long createdDate;
    public final long expiredDate;

    public StoreSession(long id, long storeId, long createdDate, long expiredDate) {
        this.id = id;
        this.storeId = storeId;
        this.createdDate = createdDate;
        this.expiredDate = expiredDate;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }
}
