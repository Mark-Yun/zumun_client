/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mark on 18. 6. 6.
 */
public class PaidDetailResponse {

    @SerializedName("tid")
    public final String tId;//	결제 고유 번호. 결제요청 API 응답에 오는 값과 동일해야 함	String
    @SerializedName("cid")
    public final String cId;//	가맹점 코드. 결제준비 API에서 요청한 값과 일치해야 함	String
    @SerializedName("status")
    public final Status status;//	결제상태값	String
    @SerializedName("partner_order_id")
    public final String partnerOrderId;//	가맹점 주문번호	String
    @SerializedName("partner_user_id")
    public final String partnerUserId;//	가맹점 회원 id	String
    @SerializedName("payment_method_type")
    public final String paymentMethodType;//	결제 수단. CARD, MONEY 중 하나	String
    @SerializedName("amount")
    public final Amount amount;//	결제 금액 정보	JSON Object
    @SerializedName("canceled_amount")
    public final Amount canceledAmount;//	취소된 금액 정보	JSON Object
    @SerializedName("cancel_available_amount")
    public final Amount cancelAvailableAmount;//	해당 결제에 대해 취소 가능 금액	JSON Object
    @SerializedName("item_name")
    public final String itemName;//	상품 이름. 최대 100자	String
    @SerializedName("item_code")
    public final String itemCode;//	상품 코드. 최대 100자	String
    @SerializedName("quantity")
    public final int quantity;//	상품 수량	Integer
    @SerializedName("created_at")
    public final String createdAt;//	결제 준비 요청 시각	Datetime
    @SerializedName("approved_at")
    public final String approvedAt;//	결제 승인 시각	Datetime
    @SerializedName("canceled_at")
    public final String canceledAt;//	결제 취소 시각	Datetime
    @SerializedName("selected_card_info")
    public final CardInfo selectedCardInfo;//	사용자가 선택한 카드정보	JSON Object
    @SerializedName("payment_action_details")
    public final List<PaymentActionsDetail> paymentActionDetails;//	결제/취소 상세	JSON Object의 List

    public PaidDetailResponse(final String tId, final String cId, final Status status, final String partnerOrderId, final String partnerUserId, final String paymentMethodType, final Amount amount, final Amount canceledAmount, final Amount cancelAvailableAmount, final String itemName, final String itemCode, final int quantity, final String createdAt, final String approvedAt, final String canceledAt, final CardInfo selectedCardInfo, final List<PaymentActionsDetail> paymentActionDetails) {
        this.tId = tId;
        this.cId = cId;
        this.status = status;
        this.partnerOrderId = partnerOrderId;
        this.partnerUserId = partnerUserId;
        this.paymentMethodType = paymentMethodType;
        this.amount = amount;
        this.canceledAmount = canceledAmount;
        this.cancelAvailableAmount = cancelAvailableAmount;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
        this.canceledAt = canceledAt;
        this.selectedCardInfo = selectedCardInfo;
        this.paymentActionDetails = paymentActionDetails;
    }
}
