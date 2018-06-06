/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mark on 18. 6. 6.
 */
public class PaidDetailRequest {

    @SerializedName("cid") public final String cId;//	가맹점 코드. 10자.	O
    @SerializedName("tid") public final String tId;//	결제 고유번호. 결제준비 API의 응답에서 얻을 수 있음	O

    public PaidDetailRequest(final String cId, final String tId) {
        this.cId = cId;
        this.tId = tId;
    }
}
