/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 18. 12. 27.
 */
@Entity(tableName = SessionStore.Schema.TABLE)
public class SessionStore extends Store {

    @SerializedName(Schema.createdDate) @ColumnInfo(name = Schema.createdDate)
    public final long createdDate;

    public SessionStore(@NonNull final String uuid, final String name, final double latitude, final double longitude, final String coverImageUrl, final String thumbnailUrl, final String phoneNumber, final String storeType, final String address, final long createdDate) {
        super(uuid, name, latitude, longitude, coverImageUrl, thumbnailUrl, phoneNumber, storeType, address);
        this.createdDate = createdDate;
    }

    public static SessionStore from(Store store) {
        return new SessionStore(store.uuid, store.name, store.latitude, store.longitude, store.coverImageUrl, store.thumbnailUrl, store.phoneNumber, store.storeType, store.address, -1);
    }

    public interface Schema extends Store.Schema {
        String TABLE = "session_store";
        String createdDate = "created_date";
    }
}
