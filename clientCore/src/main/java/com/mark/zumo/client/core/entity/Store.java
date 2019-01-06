/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

import static com.mark.zumo.client.core.entity.Store.TABLE;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = TABLE)
public class Store {
    public static final String TABLE = "store";

    @PrimaryKey @NonNull @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.name) @ColumnInfo(name = Schema.name)
    public String name;
    @SerializedName(Schema.latitude) @ColumnInfo(name = Schema.latitude)
    public double latitude;
    @SerializedName(Schema.longitude) @ColumnInfo(name = Schema.longitude)
    public double longitude;
    @SerializedName(Schema.coverImageUrl) @ColumnInfo(name = Schema.coverImageUrl)
    public String coverImageUrl;
    @SerializedName(Schema.thumbnailUrl) @ColumnInfo(name = Schema.thumbnailUrl)
    public String thumbnailUrl;
    @SerializedName(Schema.phoneNumber) @ColumnInfo(name = Schema.phoneNumber)
    public String phoneNumber;
    @SerializedName(Schema.storeType) @ColumnInfo(name = Schema.storeType)
    public String storeType;
    @SerializedName(Schema.address) @ColumnInfo(name = Schema.address)
    public String address;

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

    @Override
    public String toString() {
        return EntityHelper.toString(this);
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
}
