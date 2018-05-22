package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.dao.StoreDao;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = StoreDao.TABLE_NAME)
public class Store {

    @PrimaryKey @NonNull @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.name) @ColumnInfo(name = Schema.name)
    public final String name;
    @SerializedName(Schema.latitude) @ColumnInfo(name = Schema.latitude)
    public final long latitude;
    @SerializedName(Schema.longitude) @ColumnInfo(name = Schema.longitude)
    public final long longitude;
    @SerializedName(Schema.coverImageUrl) @ColumnInfo(name = Schema.coverImageUrl)
    public final String coverImageUrl;
    @SerializedName(Schema.thumbnailUrl) @ColumnInfo(name = Schema.thumbnailUrl)
    public final String thumbnailUrl;

    public Store(@NonNull final String uuid, final String name, final long latitude, final long longitude, final String coverImageUrl, final String thumbnailUrl) {
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

    private interface Schema {
        String uuid = "store_uuid";
        String name = "store_name";
        String latitude = "latitude";
        String longitude = "longitude";
        String coverImageUrl = "cover_image_url";
        String thumbnailUrl = "thumbnail_image_url";
    }
}
