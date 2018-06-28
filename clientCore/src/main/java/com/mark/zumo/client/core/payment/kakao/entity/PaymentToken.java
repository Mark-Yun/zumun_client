/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

import static com.mark.zumo.client.core.payment.kakao.entity.PaymentToken.Schema.table;

/**
 * Created by mark on 18. 6. 9.
 */
@Entity(tableName = table)
public class PaymentToken {
    @PrimaryKey @NonNull @ColumnInfo(name = Schema.menuOrderUuid) @SerializedName(Schema.menuOrderUuid)
    public final String menuOrderUuid;
    @ColumnInfo(name = Schema.tid) @SerializedName(Schema.tid)
    public final String tid;
    @ColumnInfo(name = Schema.pgToken) @SerializedName(Schema.pgToken)
    public final String pgToken;
    @ColumnInfo(name = Schema.kakaoAccessToken) @SerializedName(Schema.kakaoAccessToken)
    public final String kakaoAccessToken;

    public PaymentToken(@NonNull final String menuOrderUuid, final String tid, final String kakaoAccessToken, final String pgToken) {
        this.menuOrderUuid = menuOrderUuid;
        this.tid = tid;
        this.pgToken = pgToken;
        this.kakaoAccessToken = kakaoAccessToken;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, PaymentToken.class);
    }

    public interface Schema {
        String table = "payment_token";
        String menuOrderUuid = "menu_order_uuid";
        String tid = "tid";
        String pgToken = "pg_token";
        String kakaoAccessToken = "kakao_access_token";
    }
}
