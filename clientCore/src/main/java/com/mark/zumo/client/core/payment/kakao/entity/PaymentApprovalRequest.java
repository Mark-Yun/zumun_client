package com.mark.zumo.client.core.payment.kakao.entity;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

import junit.framework.Assert;

/**
 * Created by mark on 18. 5. 30.
 */
public class PaymentApprovalRequest {

    @SerializedName("cid") public final String cId;//	가맹점 코드. 10자.	O
    @SerializedName("tid") public final String tId;//	결제 고유번호. 결제준비 API의 응답에서 얻을 수 있음	O
    @SerializedName("partner_order_id") public final String partnerOrderId;//가맹점 주문번호. 결제준비 API에서 요청한 값과 일치해야 함	O
    @SerializedName("partner_user_id") public final String partnerUserId;//가맹점 회원 id. 결제준비 API에서 요청한 값과 일치해야 함	O
    @SerializedName("pg_token") public final String pgToken;//결제승인 요청을 인증하는 토큰. 사용자가 결제수단 선택 완료시 approval_url로 redirection해줄 때 pg_token을 query string으로 넘겨줌	O
    @SerializedName("payload") public final String payload;//해당 Request와 매핑해서 저장하고 싶은 값. 최대 200자	X
    @SerializedName("total_amount") public final int totalAmount;//상품총액. 결제준비 API에서 요청한 total_amount 값과 일치해야 함	X

    private PaymentApprovalRequest(Builder builder) {
        this.cId = builder.cId;
        this.tId = builder.tId;
        this.partnerOrderId = builder.partnerOrderId;
        this.partnerUserId = builder.partnerUserId;
        this.pgToken = builder.pgToken;
        this.payload = builder.payload;
        this.totalAmount = builder.totalAmount;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, PaymentApprovalRequest.class);
    }

    public class Builder {

        private String cId;//	가맹점 코드. 10자.	O
        private String tId;//	결제 고유번호. 결제준비 API의 응답에서 얻을 수 있음	O
        private String partnerOrderId;//가맹점 주문번호. 결제준비 API에서 요청한 값과 일치해야 함	O
        private String partnerUserId;//가맹점 회원 id. 결제준비 API에서 요청한 값과 일치해야 함	O
        private String pgToken;//결제승인 요청을 인증하는 토큰. 사용자가 결제수단 선택 완료시 approval_url로 redirection해줄 때 pg_token을 query string으로 넘겨줌	O
        private String payload;//해당 Request와 매핑해서 저장하고 싶은 값. 최대 200자	X
        private int totalAmount;//상품총액. 결제준비 API에서 요청한 total_amount 값과 일치해야 함	X

        public PaymentApprovalRequest build() {
            Assert.assertTrue(TextUtils.isEmpty(cId));
            Assert.assertTrue(TextUtils.isEmpty(partnerOrderId));
            Assert.assertTrue(TextUtils.isEmpty(partnerUserId));
            Assert.assertTrue(TextUtils.isEmpty(pgToken));

            return new PaymentApprovalRequest(this);
        }

        private Builder setcId(final String cId) {
            this.cId = cId;
            return this;
        }

        private Builder settId(final String tId) {
            this.tId = tId;
            return this;
        }

        private Builder setPartnerOrderId(final String partnerOrderId) {
            this.partnerOrderId = partnerOrderId;
            return this;
        }

        private Builder setPartnerUserId(final String partnerUserId) {
            this.partnerUserId = partnerUserId;
            return this;
        }

        private Builder setPgToken(final String pgToken) {
            this.pgToken = pgToken;
            return this;
        }

        private Builder setPayload(final String payload) {
            this.payload = payload;
            return this;
        }

        private Builder setTotalAmount(final int totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }
    }
}
