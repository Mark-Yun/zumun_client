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
public class PaymentActionsDetail {
    @SerializedName("aid")
    public final String aId;//	Request 고유 번호	String
    @SerializedName("approvedAt")
    public final String approved_at;//	거래시간	String
    @SerializedName("amount")
    public final int amount;//	결제/취소 총액	Integer
    @SerializedName("pointAmount")
    public final int point_amount;//	결제/취소 포인트 금액	Integer
    @SerializedName("discountAmount")
    public final int discount_amount;//	할인 금액	Integer
    @SerializedName("payment_action_type")
    public final PaymentActionType paymentActionType;//	결제 타입. PAYMENT(결제), CANCEL(결제취소), ISSUED_SID(SID 발급) 중 하나	String
    @SerializedName("payload")
    public final String payload;//	Request로 전달한 값;//	String

    public PaymentActionsDetail(final String aId, final String approved_at, final int amount, final int point_amount, final int discount_amount, final PaymentActionType paymentActionType, final String payload) {
        this.aId = aId;
        this.approved_at = approved_at;
        this.amount = amount;
        this.point_amount = point_amount;
        this.discount_amount = discount_amount;
        this.paymentActionType = paymentActionType;
        this.payload = payload;
    }
}
