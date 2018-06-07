/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao;

import com.mark.zumo.client.core.payment.kakao.entity.CancelResponse;
import com.mark.zumo.client.core.payment.kakao.entity.PaidDetailResponse;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentApprovalResponse;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentReadyResponse;

import java.util.List;

import io.reactivex.Maybe;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by mark on 18. 5. 30.
 */
public interface KakaoPayService {
    String URL = "https://kapi.kakao.com";

    @FormUrlEncoded
    @POST("/v1/payment/ready")
    Maybe<PaymentReadyResponse> readyPayment(@Field("cid") String cId,
                                             @Field("partner_order_id") String partnerOrderId,
                                             @Field("partner_user_id") String partnerUserId,
                                             @Field("item_name") String itemName,
                                             @Field("item_code") String itemCode,
                                             @Field("quantity") int quantity,
                                             @Field("total_amount") int totalAmount,
                                             @Field("tax_free_amount") int taxFreeAmount,
                                             @Field("vat_amount") int vatAmount,
                                             @Field("approval_url") String approvalUrl,
                                             @Field("cancel_url") String cancelUrl,
                                             @Field("fail_url") String failUrl,
                                             @Field("available_cards") List<String> availableCards,
                                             @Field("payment_method_type") String paymentMethodType,
                                             @Field("install_month") int installMonth,
                                             @Field("custom_json") String customJson);

    @FormUrlEncoded
    @POST("/v1/payment/approve")
    Maybe<PaymentApprovalResponse> approvalPayment(@Field("cid") String cId,
                                                   @Field("tid") String tId,
                                                   @Field("partner_order_id") String partnerOrderId,
                                                   @Field("partner_user_id") String partnerUserId,
                                                   @Field("pg_token") String pgToken,
                                                   @Field("payload") String payload,
                                                   @Field("total_amount") int totalAmount);

    @FormUrlEncoded
    @POST("/v1/payment/order")
    Maybe<PaidDetailResponse> paiedDetail(@Field("cid") String cId,
                                          @Field("tid") String tId);

    @FormUrlEncoded
    @POST("/v1/payment/cancel")
    Maybe<CancelResponse> cancel(@Field("cid") String cId,
                                 @Field("tid") String tId,
                                 @Field("cancel_amount") int cancelAmount,
                                 @Field("cancel_tax_free_amount") int cancelTaxFreeAmount,
                                 @Field("cancel_vat_amount") int cancelVatAmount,
                                 @Field("cancel_available_amount") int cancelAvailableAmount,
                                 @Field("payload") String payload);
}
