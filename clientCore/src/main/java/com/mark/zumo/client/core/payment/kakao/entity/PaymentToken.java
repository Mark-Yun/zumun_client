/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 18. 6. 9.
 */
public class PaymentToken {
    @SerializedName("menu_order_uuid") public final String menuOrderUuid;
    @SerializedName("tid") public final String tid;
    @SerializedName("pg_token") public final String pgToken;
    @SerializedName("kakao_access_token") public final String kakaoAccessToken;

    public PaymentToken(final String menuOrderUuid, final String tid, final String kakaoAccessToken, final String pgToken) {
        this.menuOrderUuid = menuOrderUuid;
        this.tid = tid;
        this.pgToken = pgToken;
        this.kakaoAccessToken = kakaoAccessToken;
    }
}
