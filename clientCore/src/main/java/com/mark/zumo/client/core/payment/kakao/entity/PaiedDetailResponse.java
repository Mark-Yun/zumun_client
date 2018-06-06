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
public class PaiedDetailResponse {

    @SerializedName("tid")
    public final String tId;//	결제 고유 번호. 결제요청 API 응답에 오는 값과 동일해야 함	String
    @SerializedName("cid")
    public final String cId;//	가맹점 코드. 결제준비 API에서 요청한 값과 일치해야 함	String
    @SerializedName("status")
    public final Status status;//	결제상태값	String
    @SerializedName("partner_order_id")
    public final String partnerOrderId;//	가맹점 주문번호	String
    @SerializedName("partnerUserId")
    public final String partner_user_id;//	가맹점 회원 id	String
    @SerializedName("paymentMethodType")
    public final String payment_method_type;//	결제 수단. CARD, MONEY 중 하나	String
    @SerializedName("amount")
    public final Amount amount;//	결제 금액 정보	JSON Object
    @SerializedName("canceledAmount")
    public final Amount canceled_amount;//	취소된 금액 정보	JSON Object
    @SerializedName("cancelAvailableAmount")
    public final Amount cancel_available_amount;//	해당 결제에 대해 취소 가능 금액	JSON Object
    @SerializedName("itemName")
    public final String item_name;//	상품 이름. 최대 100자	String
    @SerializedName("itemCode")
    public final String item_code;//	상품 코드. 최대 100자	String
    @SerializedName("quantity")
    public final int quantity;//	상품 수량	Integer
    @SerializedName("created_at")
    public final String createdAt;//	결제 준비 요청 시각	Datetime
    @SerializedName("approved_at")
    public final String approvedAt;//	결제 승인 시각	Datetime
    @SerializedName("canceled_at")
    public final String canceledAt;//	결제 취소 시각	Datetime
    @SerializedName("selectedCardInfo")
    public final CardInfo selected_card_info;//	사용자가 선택한 카드정보	JSON Object
    @SerializedName("paymentActionDetails")
    public final String payment_action_details;//	결제/취소 상세	JSON Object의 List

    public PaiedDetailResponse(final String tId, final String cId, final Status status, final String partnerOrderId, final String partner_user_id, final String payment_method_type, final Amount amount, final Amount canceled_amount, final Amount cancel_available_amount, final String item_name, final String item_code, final int quantity, final String createdAt, final String approvedAt, final String canceledAt, final CardInfo selected_card_info, final String payment_action_details) {
        this.tId = tId;
        this.cId = cId;
        this.status = status;
        this.partnerOrderId = partnerOrderId;
        this.partner_user_id = partner_user_id;
        this.payment_method_type = payment_method_type;
        this.amount = amount;
        this.canceled_amount = canceled_amount;
        this.cancel_available_amount = cancel_available_amount;
        this.item_name = item_name;
        this.item_code = item_code;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
        this.canceledAt = canceledAt;
        this.selected_card_info = selected_card_info;
        this.payment_action_details = payment_action_details;
    }
}
