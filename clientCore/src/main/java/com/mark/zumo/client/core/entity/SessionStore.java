/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Created by mark on 18. 12. 27.
 */
@Entity(tableName = SessionStore.Schema.TABLE)
public class SessionStore extends Store {

    public SessionStore(@NonNull final String uuid, final String name, final double latitude, final double longitude, final String coverImageUrl, final String thumbnailUrl, final String phoneNumber, final String storeType, final String address) {
        super(uuid, name, latitude, longitude, coverImageUrl, thumbnailUrl, phoneNumber, storeType, address);
    }

    public static SessionStore from(Store store) {
        return new SessionStore(store.uuid, store.name, store.latitude, store.longitude, store.coverImageUrl, store.thumbnailUrl, store.phoneNumber, store.storeType, store.address);
    }

    public interface Schema extends Store.Schema {
        String TABLE = "session_store";
    }
}
