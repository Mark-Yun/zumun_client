package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.Entity;

import com.mark.zumo.client.core.entity.user.StoreUser;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity
public class StoreClerk extends StoreUser {

    public StoreClerk(long id, String name, long createdDate) {
        super(id, name, createdDate);
    }
}
