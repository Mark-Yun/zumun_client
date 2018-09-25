/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao.entity;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 5. 30.
 */
public class PaymentApprovalResponse {

    @SerializedName("aid") public final String aId;//Request 고유 번호
    @SerializedName("tid") public final String tId;//결제 고유 번호
    @SerializedName("cid") public final String cId;//가맹점 코드
    @SerializedName("sid") public final String sId;//subscription id. 정기(배치)결제 CID로 결제요청한 경우 발급	
    @SerializedName("partner_order_id") public final String partnerOrderId;//가맹점 주문번호
    @SerializedName("partner_user_id") public final String partnerUserId;//가맹점 회원 id
    @SerializedName("payment_method_type") public final String paymentMethodType;//결제 수단. CARD, MONEY 중 하나
    @SerializedName("amount") public final Amount amount;//결제 금액 정보
    @SerializedName("card_info") public final CardInfo cardInfo;//결제 상세 정보(결제수단이 카드일 경우만 포함)
    @SerializedName("item_name") public final String itemName;//상품 이름. 최대 100자
    @SerializedName("item_code") public final String itemCode;//상품 코드. 최대 100자
    @SerializedName("quantity") public final String quantity;//상품 수량
    @SerializedName("created_at") public final String createdAt;//결제 준비 요청 시각
    @SerializedName("approved_at") public final String approvedAt;//결제 승인 시각
    @SerializedName("payload") public final String payload;//Request로 전달한 값

    public PaymentApprovalResponse(final String aId, final String tId, final String cId, final String sId, final String partnerOrderId, final String partnerUserId, final String paymentMethodType, final Amount amount, final CardInfo cardInfo, final String itemName, final String itemCode, final String quantity, final String createdAt, final String approvedAt, final String payload) {
        this.aId = aId;
        this.tId = tId;
        this.cId = cId;
        this.sId = sId;
        this.partnerOrderId = partnerOrderId;
        this.partnerUserId = partnerUserId;
        this.paymentMethodType = paymentMethodType;
        this.amount = amount;
        this.cardInfo = cardInfo;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

}
