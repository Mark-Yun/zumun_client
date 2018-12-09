/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.app;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by mark on 18. 8. 15.
 * for debug build config
 */
public enum AppConfig {
    DEBUG(
            ConfigConstant.APP_SERVER_DEV_URL,
            ConfigConstant.APP_SERVER_DEV_API_KEY,
            ConfigConstant.PAYMENT_SERVICE_DEV_URL,
            ConfigConstant.KAKAO_PAY_TEST_CID,
            ConfigConstant.LOGGER_LEVEL_BODY
    ),
    RELEASE(
            ConfigConstant.APP_SERVER_PRD_URL,
            ConfigConstant.APP_SERVER_PRD_API_KEY,
            ConfigConstant.PAYMENT_SERVICE_PRD_URL,
            ConfigConstant.KAKAO_PAY_TEST_CID,
            ConfigConstant.LOGGER_LEVEL_NONE
    );


    public final String appServerUrl;
    public final String appServerApiKey;
    public final String paymentServiceUrl;
    public final String kakaoPayCId;
    public final HttpLoggingInterceptor.Level httpLoggerLevel;

    AppConfig(final String appServerUrl, final String appServerApiKey, final String paymentServiceUrl, final String kakaoPayCId, final HttpLoggingInterceptor.Level httpLoggerLevel) {
        this.appServerUrl = appServerUrl;
        this.appServerApiKey = appServerApiKey;
        this.paymentServiceUrl = paymentServiceUrl;
        this.kakaoPayCId = kakaoPayCId;
        this.httpLoggerLevel = httpLoggerLevel;
    }
}
