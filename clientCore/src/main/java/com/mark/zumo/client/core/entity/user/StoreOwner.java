package com.mark.zumo.client.core.entity.user;

import android.arch.persistence.room.Entity;

import com.mark.zumo.client.core.entity.EntityHelper;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity
public class StoreOwner extends StoreUser {

    public StoreOwner(long id, String name, long createdDate) {
        super(id, name, createdDate);
    }
}
