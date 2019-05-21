/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao.entity;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.database.entity.util.EntityHelper;

import org.junit.Assert;

import java.util.List;

/**
 * Created by mark on 18. 5. 30.
 */
public class PaymentReadyRequest {

    public static final String REDIRECT_URL_SUCCESS = "https://developers.kakao.com/success";
    public static final String REDIRECT_URL_FAIL = "https://developers.kakao.com/fail";
    public static final String REDIRECT_URL_CANCEL = "https://developers.kakao.com/cancel";

    @SerializedName("cid") public final String cId;//가맹점 코드. 10자. O
    @SerializedName("partner_order_id") public final String partnerOrderId;//가맹점 주문번호. 최대 100자	O
    @SerializedName("partner_user_id") public final String partnerUserId;//가맹점 회원 id. 최대 100자	O
    @SerializedName("item_name") public final String itemName;//상품명. 최대 100자	O
    @SerializedName("item_code") public final String itemCode;//상품코드. 최대 100자	X
    @SerializedName("quantity") public final int quantity;//	상품 수량	O
    @SerializedName("total_amount") public final int totalAmount;//상품 총액	O
    @SerializedName("tax_free_amount") public final int taxFreeAmount;//상품 비과세 금액	O
    @SerializedName("vat_amount") public final int vatAmount;//상품 부가세 금액(안보낼 경우 (상품총액 - 상품 비과세 금액)/11 : 소숫점이하 반올림)	X
    @SerializedName("approval_url") public final String approvalUrl;//결제 성공시 redirect url. 최대 255자	O
    @SerializedName("cancel_url") public final String cancelUrl;//결제 취소시 redirect url. 최대 255자	O
    @SerializedName("fail_url") public final String failUrl;//결제 실패시 redirect url. 최대 255자	O
    @SerializedName("available_cards") public final List<String> availableCards;//카드사 제한 목록(없을 경우 전체) 현재 SHINHAN, KB, HYUNDAI, LOTTE, SAMSUNG, NH, BC, HANA, CITI, KAKAOBANK, KAKAOPAY 지원. ex) [“HANA”, “BC”] X
    @SerializedName("payment_method_type") public final String paymentMethodType;//결제 수단 제한(없을 경우 전체) CARD, MONEY 중 하나	X
    @SerializedName("install_month") public final int installMonth;//카드할부개월수. 0~12(개월) 사이의 값
    @SerializedName("custom_json") public final String customJson;//결제화면에 보여주고 싶은 custom message. 사전협의가 필요한 값

    private PaymentReadyRequest(Builder builder) {
        this.cId = builder.cId;
        this.partnerOrderId = builder.partnerOrderId;
        this.partnerUserId = builder.partnerUserId;
        this.itemName = builder.itemName;
        this.itemCode = builder.itemCode;
        this.quantity = builder.quantity;
        this.totalAmount = builder.totalAmount;
        this.taxFreeAmount = builder.taxFreeAmount;
        this.vatAmount = builder.vatAmount;
        this.approvalUrl = builder.approvalUrl;
        this.cancelUrl = builder.cancelUrl;
        this.failUrl = builder.failUrl;
        this.availableCards = builder.availableCards;
        this.paymentMethodType = builder.paymentMethodType;
        this.installMonth = builder.installMonth;
        this.customJson = builder.customJson;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    public static class Builder {

        private String cId;//가맹점 코드. 10자. O
        private String partnerOrderId;//가맹점 주문번호. 최대 100자	O
        private String partnerUserId;//가맹점 회원 id. 최대 100자	O
        private String itemName;//상품명. 최대 100자	O
        private String itemCode;//상품코드. 최대 100자	X
        private int quantity;//	상품 수량	O
        private int totalAmount;//상품 총액	O
        private int taxFreeAmount;//상품 비과세 금액	O
        private int vatAmount;//상품 부가세 금액(안보낼 경우 (상품총액 - 상품 비과세 금액)/11 : 소숫점이하 반올림)	X
        private String approvalUrl;//결제 성공시 redirect url. 최대 255자	O
        private String cancelUrl;//결제 취소시 redirect url. 최대 255자	O
        private String failUrl;//결제 실패시 redirect url. 최대 255자	O
        private List<String> availableCards;//카드사 제한 목록(없을 경우 전체) 현재 SHINHAN, KB, HYUNDAI, LOTTE, SAMSUNG, NH, BC, HANA, CITI, KAKAOBANK, KAKAOPAY 지원. ex) [“HANA”, “BC”] X
        private String paymentMethodType;//결제 수단 제한(없을 경우 전체) CARD, MONEY 중 하나	X
        private int installMonth;//카드할부개월수. 0~12(개월) 사이의 값 X
        private String customJson;//결제화면에 보여주고 싶은 custom message. 사전협의가 필요한 값 X

        public PaymentReadyRequest build() {
            Assert.assertFalse(TextUtils.isEmpty(cId));
            Assert.assertFalse(TextUtils.isEmpty(partnerOrderId));
            Assert.assertFalse(TextUtils.isEmpty(partnerUserId));
            Assert.assertFalse(TextUtils.isEmpty(itemName));
            Assert.assertNotEquals(0, quantity);
            Assert.assertNotEquals(0, totalAmount);
            Assert.assertFalse(TextUtils.isEmpty(approvalUrl));
            Assert.assertFalse(TextUtils.isEmpty(cancelUrl));
            Assert.assertFalse(TextUtils.isEmpty(failUrl));
            return new PaymentReadyRequest(this);
        }

        public Builder setCId(final String cId) {
            this.cId = cId;
            return this;
        }

        public Builder setPartnerOrderId(final String partnerOrderId) {
            this.partnerOrderId = partnerOrderId;
            return this;
        }

        public Builder setPartnerUserId(final String partnerUserId) {
            this.partnerUserId = partnerUserId;
            return this;
        }

        public Builder setItemName(final String itemName) {
            this.itemName = itemName;
            return this;
        }

        public Builder setItemCode(final String itemCode) {
            this.itemCode = itemCode;
            return this;
        }

        public Builder setQuantity(final int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder setTotalAmount(final int totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder setTaxFreeAmount(final int taxFreeAmount) {
            this.taxFreeAmount = taxFreeAmount;
            return this;
        }

        public Builder setVatAmount(final int vatAmount) {
            this.vatAmount = vatAmount;
            return this;
        }

        public Builder setApprovalUrl(final String approvalUrl) {
            this.approvalUrl = approvalUrl;
            return this;
        }

        public Builder setCancelUrl(final String cancelUrl) {
            this.cancelUrl = cancelUrl;
            return this;
        }

        public Builder setFailUrl(final String failUrl) {
            this.failUrl = failUrl;
            return this;
        }

        public Builder setAvailableCards(final List<String> availableCards) {
            this.availableCards = availableCards;
            return this;
        }

        public Builder setPaymentMethodType(final String paymentMethodType) {
            this.paymentMethodType = paymentMethodType;
            return this;
        }

        public Builder setInstallMonth(final int installMonth) {
            this.installMonth = installMonth;
            return this;
        }

        public Builder setCustomJson(final String customJson) {
            this.customJson = customJson;
            return this;
        }
    }
}
