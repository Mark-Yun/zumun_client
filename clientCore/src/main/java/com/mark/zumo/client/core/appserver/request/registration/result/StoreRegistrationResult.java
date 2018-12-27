/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.registration.result;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 18. 12. 26.
 */
@Entity(tableName = StoreRegistrationResult.Schema.table)
public class StoreRegistrationResult {

    @PrimaryKey @SerializedName(Schema.id) @ColumnInfo(name = Schema.id)
    public final long id;
    @SerializedName(Schema.requestId) @ColumnInfo(name = Schema.requestId)
    public final long requestId;
    @SerializedName(Schema.result) @ColumnInfo(name = Schema.result)
    public final String result;
    @SerializedName(Schema.comment) @ColumnInfo(name = Schema.comment)
    public final String comment;
    @SerializedName(Schema.createdDate) @ColumnInfo(name = Schema.createdDate)
    public final String createdDate;

    public StoreRegistrationResult(final long id, final long requestId, final String result, final String comment, final String createdDate) {
        this.id = id;
        this.requestId = requestId;
        this.result = result;
        this.comment = comment;
        this.createdDate = createdDate;
    }

    public interface Schema {
        String table = "store_registration_result";
        String id = "id";
        String requestId = "request_id";
        String result = "result";
        String comment = "comment";
        String createdDate = "created_date";
    }
}
