package com.mark.zumo.client.core.payment.kakao.entity;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 5. 30.
 */
public class PaymentReadyResponse {

    @SerializedName("tid") public final String tId;//결제 고유 번호. 20자
    @SerializedName("next_redirect_app_url") public final String nextRedirectAppUrl;//요청한 클라이언트가 모바일 앱일 경우 해당 url을 통해 카카오톡 결제페이지를 띄움	String
    @SerializedName("next_redirect_mobile_url") public final String nextRedirectMobileRrl;//요청한 클라이언트가 모바일 웹일 경우 해당 url을 통해 카카오톡 결제페이지를 띄움	String
    @SerializedName("next_redirect_pc_url") public final String nextRedirectPcUrl;//요청한 클라이언트가 pc 웹일 경우 redirect. 카카오톡으로 TMS를 보내기 위한 사용자입력화면이으로 redirect	String
    @SerializedName("android_app_scheme") public final String androidAppScheme;//카카오페이 결제화면으로 이동하는 안드로이드 앱스킴	String
    @SerializedName("ios_app_scheme") public final String iosAppScheme;//카카오페이 결제화면으로 이동하는 iOS 앱스킴	String
    @SerializedName("created_at") public final String createdAt;//결제 준비 요청 시간	Datetime

    private PaymentReadyResponse(final String tId, final String nextRedirectAppUrl, final String nextRedirectMobileRrl, final String nextRedirectPcUrl, final String androidAppScheme, final String iosAppScheme, final String createdAt) {
        this.tId = tId;
        this.nextRedirectAppUrl = nextRedirectAppUrl;
        this.nextRedirectMobileRrl = nextRedirectMobileRrl;
        this.nextRedirectPcUrl = nextRedirectPcUrl;
        this.androidAppScheme = androidAppScheme;
        this.iosAppScheme = iosAppScheme;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, PaymentReadyResponse.class);
    }
}
