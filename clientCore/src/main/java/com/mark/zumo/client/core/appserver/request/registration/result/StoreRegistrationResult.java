/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.registration.result;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.R;

/**
 * Created by mark on 18. 12. 26.
 */
@Entity(tableName = StoreRegistrationResult.Schema.table)
public class StoreRegistrationResult {

    @NonNull @PrimaryKey @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.storeRegistrationRequestUuid) @ColumnInfo(name = Schema.storeRegistrationRequestUuid)
    public final String storeRegistrationRequestUuid;
    @SerializedName(Schema.status) @ColumnInfo(name = Schema.status)
    public final String status;
    @SerializedName(Schema.comment) @ColumnInfo(name = Schema.comment)
    public final String comment;
    @SerializedName(Schema.createdDate) @ColumnInfo(name = Schema.createdDate)
    public final String createdDate;

    public StoreRegistrationResult(@NonNull final String uuid, final String storeRegistrationRequestUuid, final String status, final String comment, final String createdDate) {
        this.uuid = uuid;
        this.storeRegistrationRequestUuid = storeRegistrationRequestUuid;
        this.status = status;
        this.comment = comment;
        this.createdDate = createdDate;
    }

    public enum Status {

        REQUESTED(R.string.store_registration_result_result_requested),
        APPROVED(R.string.store_registration_result_result_approved),
        REJECTED(R.string.store_registration_result_result_rejected);

        public final int stringRes;

        Status(final int stringRes) {
            this.stringRes = stringRes;
        }
    }

    public interface Schema {
        String table = "store_registration_result";
        String uuid = "store_registration_result_uuid";
        String storeRegistrationRequestUuid = "store_registration_request_uuid";
        String status = "registration_status";
        String comment = "comment";
        String createdDate = "created_date";
    }
}
