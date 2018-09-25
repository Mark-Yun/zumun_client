/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao.entity;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 6. 6.
 */
public class Amount {

    @SerializedName("total") public final int total;//전체 결제 금액
    @SerializedName("tax_free") public final int taxFree;//비과세 금액
    @SerializedName("vat") public final int vat;//부가세 금액
    @SerializedName("point") public final int point;//사용한 포인트 금액
    @SerializedName("discount") public final int discount;//할인 금액

    public Amount(final int total, final int taxFree, final int vat, final int point, final int discount) {
        this.total = total;
        this.taxFree = taxFree;
        this.vat = vat;
        this.point = point;
        this.discount = discount;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }
}
