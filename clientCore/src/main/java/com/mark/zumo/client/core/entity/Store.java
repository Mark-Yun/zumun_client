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
    public final String name;
    @SerializedName(Schema.latitude) @ColumnInfo(name = Schema.latitude)
    public final double latitude;
    @SerializedName(Schema.longitude) @ColumnInfo(name = Schema.longitude)
    public final double longitude;
    @SerializedName(Schema.coverImageUrl) @ColumnInfo(name = Schema.coverImageUrl)
    public final String coverImageUrl;
    @SerializedName(Schema.thumbnailUrl) @ColumnInfo(name = Schema.thumbnailUrl)
    public final String thumbnailUrl;

    public Store(@NonNull final String uuid, final String name, final double latitude, final double longitude, final String coverImageUrl, final String thumbnailUrl) {
        this.uuid = uuid;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.coverImageUrl = coverImageUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }

    public interface Schema {
        String uuid = TABLE + "_uuid";
        String name = TABLE + "_name";
        String latitude = "latitude";
        String longitude = "longitude";
        String coverImageUrl = "cover_image_url";
        String thumbnailUrl = "thumbnail_image_url";
    }
}
