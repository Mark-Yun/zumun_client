/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.core.app;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by mark on 18. 8. 29.
 */
final class ConfigConstant {

    static final String APP_SERVER_DEV_URL = "https://akxjj18zh8.execute-api.ap-northeast-2.amazonaws.com/api/";
    static final String APP_SERVER_PROD_URL = "https://w8nvfkffv5.execute-api.ap-northeast-2.amazonaws.com/api/";

    static final String PAYMENT_SERVICE_DEV_URL = "https://9e9owpd0lk.execute-api.ap-northeast-2.amazonaws.com/api/";
    static final String PAYMENT_SERVICE_PROD_URL = "https://nyua4vsg24.execute-api.ap-northeast-2.amazonaws.com/api/";

    static final String KAKAO_PAY_TEST_CID = "TC0ONETIME";

    static final HttpLoggingInterceptor.Level LOGGER_LEVEL_BODY = HttpLoggingInterceptor.Level.BODY;
    static final HttpLoggingInterceptor.Level LOGGER_LEVEL_NONE = HttpLoggingInterceptor.Level.NONE;
}
