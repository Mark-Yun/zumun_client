/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.entity.user;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.util.EntityHelper;

import static com.mark.zumo.client.core.entity.user.GuestUser.TABLE;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = TABLE)
public class GuestUser {
    public static final String TABLE = "guest_user";
    @PrimaryKey @NonNull
    public final String uuid;

    public GuestUser(@NonNull final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }
}
