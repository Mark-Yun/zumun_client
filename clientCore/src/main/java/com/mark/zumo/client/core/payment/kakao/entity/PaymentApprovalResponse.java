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
        return EntityHelper.toString(this, PaymentApprovalResponse.class);
    }

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
            return EntityHelper.toString(this, Amount.class);
        }
    }

    public class CardInfo {

        @SerializedName("purchase_corp") public final String purchaseCorp;//매입카드사 한글명
        @SerializedName("purchase_corp_code") public final String purchaseCorpCode;//매입카드사 코드
        @SerializedName("issuer_corp") public final String issuerCorp;//카드발급사 한글명
        @SerializedName("issuer_corp_code") public final String issuerCorpCode;//카드발급사 코드
        @SerializedName("bin") public final String bin;//카드 BIN
        @SerializedName("card_type") public final String cardType;//카드타입
        @SerializedName("install_month") public final String installMonth;//할부개월수
        @SerializedName("approved_id") public final String approvedId;//카드사 승인번호
        @SerializedName("card_mid") public final String cardMId;//카드사 가맹점번호
        @SerializedName("interest_free_install") public final String interestFreeInstall;//무이자할부 여부(Y/N)
        @SerializedName("card_item_code") public final String cardItemCode;//카드 상품 코드

        public CardInfo(final String purchaseCorp, final String purchaseCorpCode, final String issuerCorp, final String issuerCorpCode, final String bin, final String cardType, final String installMonth, final String approvedId, final String cardMId, final String interestFreeInstall, final String cardItemCode) {
            this.purchaseCorp = purchaseCorp;
            this.purchaseCorpCode = purchaseCorpCode;
            this.issuerCorp = issuerCorp;
            this.issuerCorpCode = issuerCorpCode;
            this.bin = bin;
            this.cardType = cardType;
            this.installMonth = installMonth;
            this.approvedId = approvedId;
            this.cardMId = cardMId;
            this.interestFreeInstall = interestFreeInstall;
            this.cardItemCode = cardItemCode;
        }

        @Override
        public String toString() {
            return EntityHelper.toString(this, CardInfo.class);
        }
    }
}
