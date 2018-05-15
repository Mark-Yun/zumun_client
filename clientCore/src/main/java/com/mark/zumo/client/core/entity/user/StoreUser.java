package com.mark.zumo.client.core.entity.user;

import android.arch.persistence.room.Entity;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = "store_user")
public class StoreUser extends User {

    public StoreUser(long id) {
        super(id);
    }
}
