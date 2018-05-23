package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.mark.zumo.client.core.dao.MenuOrderDao;
import com.mark.zumo.client.core.entity.util.EntityHelper;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = MenuOrderDao.TABLE_NAME)
public class MenuOrder implements Serializable {

    @PrimaryKey public final long id;
    public final long customerUserSessionId;
    public final long storeSessionId;
    public final List<Long> menuItemIds;
    public final long createdDate;
    public final int totalPrice;

    public MenuOrder(long id, long customerUserSessionId, long storeSessionId, List<Long> menuItemIds, long createdDate, int totalPrice) {
        this.id = id;
        this.customerUserSessionId = customerUserSessionId;
        this.storeSessionId = storeSessionId;
        this.menuItemIds = menuItemIds;
        this.createdDate = createdDate;
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }
}
