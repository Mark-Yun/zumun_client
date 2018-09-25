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
import com.mark.zumo.client.core.entity.util.EntityHelper;

import static com.mark.zumo.client.core.entity.VisitStore.TABLE;

/**
 * Created by mark on 18. 6. 1.
 */
@Entity(tableName = TABLE, primaryKeys = {VisitStore.Schema.storeUuid, VisitStore.Schema.visitDate})
public class VisitStore {

    public static final String TABLE = "visit_store";

    @NonNull @SerializedName(Schema.storeUuid) @ColumnInfo(name = Schema.storeUuid)
    public final String storeUuid;
    @NonNull @SerializedName(Schema.visitDate) @ColumnInfo(name = Schema.visitDate)
    public final String visitDate;

    public VisitStore(@NonNull final String storeUuid, @NonNull final String visitDate) {
        this.storeUuid = storeUuid;
        this.visitDate = visitDate;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    interface Schema {
        String uuid = "visit_store_uuid";
        String storeUuid = "store_uuid";
        String visitDate = "visit_date";
    }
}
