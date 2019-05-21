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

import static com.mark.zumo.client.core.database.entity.Store.TABLE;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = TABLE)
public class Store {
    public static final String TABLE = "store";

    @PrimaryKey @NonNull @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.name) @ColumnInfo(name = Schema.name)
    public final String name;
    @SerializedName(Schema.latitude) @ColumnInfo(name = Schema.latitude)
    public final double latitude;
    @SerializedName(Schema.longitude) @ColumnInfo(name = Schema.longitude)
    public final double longitude;
    @SerializedName(Schema.coverImageUrl) @ColumnInfo(name = Schema.coverImageUrl)
    public final String coverImageUrl;
    @SerializedName(Schema.thumbnailUrl) @ColumnInfo(name = Schema.thumbnailUrl)
    public final String thumbnailUrl;
    @SerializedName(Schema.phoneNumber) @ColumnInfo(name = Schema.phoneNumber)
    public final String phoneNumber;
    @SerializedName(Schema.storeType) @ColumnInfo(name = Schema.storeType)
    public final String storeType;
    @SerializedName(Schema.address) @ColumnInfo(name = Schema.address)
    public final String address;

    public Store(@NonNull final String uuid, final String name, final double latitude, final double longitude, final String coverImageUrl, final String thumbnailUrl, final String phoneNumber, final String storeType, final String address) {
        this.uuid = uuid;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.coverImageUrl = coverImageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.phoneNumber = phoneNumber;
        this.storeType = storeType;
        this.address = address;
    }

    public static Store from(SessionStore sessionStore) {
        return new Store(sessionStore.uuid, sessionStore.name, sessionStore.latitude, sessionStore.longitude, sessionStore.coverImageUrl, sessionStore.thumbnailUrl, sessionStore.phoneNumber, sessionStore.storeType, sessionStore.address);
    }
    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return new EntityComparator<>().test(this, obj);
    }

    public interface Schema {
        String uuid = "store_uuid";
        String name = "store_name";
        String latitude = "latitude";
        String longitude = "longitude";
        String coverImageUrl = "cover_image_url";
        String thumbnailUrl = "thumbnail_image_url";
        String phoneNumber = "store_phone_number";
        String storeType = "store_type";
        String address = "address";
    }

    public static class Builder {

        private String uuid = "";
        private String name = "";
        private double latitude = 0;
        private double longitude = 0;
        private String coverImageUrl = "";
        private String thumbnailUrl = "";
        private String phoneNumber = "";
        private String storeType = "";
        private String address = "";

        public Builder() {
        }

        public static Builder from(Store store) {
            Builder builder = new Builder();
            builder.uuid = store.uuid;
            builder.name = store.name;
            builder.latitude = store.latitude;
            builder.longitude = store.longitude;
            builder.coverImageUrl = store.coverImageUrl;
            builder.thumbnailUrl = store.thumbnailUrl;
            builder.phoneNumber = store.phoneNumber;
            builder.storeType = store.storeType;
            builder.address = store.address;
            return builder;
        }

        public Builder setUuid(final String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setLatitude(final double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(final double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setCoverImageUrl(final String coverImageUrl) {
            this.coverImageUrl = coverImageUrl;
            return this;
        }

        public Builder setThumbnailUrl(final String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public Builder setPhoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setStoreType(final String storeType) {
            this.storeType = storeType;
            return this;
        }

        public Builder setAddress(final String address) {
            this.address = address;
            return this;
        }

        public Store build() {
            return new Store(uuid, name, latitude, longitude, coverImageUrl, thumbnailUrl, phoneNumber, storeType, address);
        }
    }
}
