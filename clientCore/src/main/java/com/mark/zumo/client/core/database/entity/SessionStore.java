/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.database.entity.util.EntityComparator;
import com.mark.zumo.client.core.database.entity.util.EntityHelper;

/**
 * Created by mark on 18. 12. 27.
 */
@Entity(tableName = SessionStore.Schema.TABLE)
public class SessionStore {

    @PrimaryKey @NonNull @SerializedName(Store.Schema.uuid) @ColumnInfo(name = Store.Schema.uuid)
    public final String uuid;
    @SerializedName(Store.Schema.name) @ColumnInfo(name = Store.Schema.name)
    public final String name;
    @SerializedName(Store.Schema.latitude) @ColumnInfo(name = Store.Schema.latitude)
    public final double latitude;
    @SerializedName(Store.Schema.longitude) @ColumnInfo(name = Store.Schema.longitude)
    public final double longitude;
    @SerializedName(Store.Schema.coverImageUrl) @ColumnInfo(name = Store.Schema.coverImageUrl)
    public final String coverImageUrl;
    @SerializedName(Store.Schema.thumbnailUrl) @ColumnInfo(name = Store.Schema.thumbnailUrl)
    public final String thumbnailUrl;
    @SerializedName(Store.Schema.phoneNumber) @ColumnInfo(name = Store.Schema.phoneNumber)
    public final String phoneNumber;
    @SerializedName(Store.Schema.storeType) @ColumnInfo(name = Store.Schema.storeType)
    public final String storeType;
    @SerializedName(Store.Schema.address) @ColumnInfo(name = Store.Schema.address)
    public final String address;

    @SerializedName(Schema.createdDate) @ColumnInfo(name = Schema.createdDate)
    public final long createdDate;

    public SessionStore(@NonNull final String uuid, final String name, final double latitude, final double longitude, final String coverImageUrl, final String thumbnailUrl, final String phoneNumber, final String storeType, final String address, final long createdDate) {
        this.uuid = uuid;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.coverImageUrl = coverImageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.phoneNumber = phoneNumber;
        this.storeType = storeType;
        this.address = address;
        this.createdDate = System.currentTimeMillis();
    }

    public static SessionStore from(Store store) {
        return new SessionStore(store.uuid, store.name, store.latitude, store.longitude, store.coverImageUrl, store.thumbnailUrl, store.phoneNumber, store.storeType, store.address, -1);
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return new EntityComparator<>().test(this, obj);
    }

    public interface Schema extends Store.Schema {
        String TABLE = "session_store";
        String createdDate = "created_date";
    }
}
