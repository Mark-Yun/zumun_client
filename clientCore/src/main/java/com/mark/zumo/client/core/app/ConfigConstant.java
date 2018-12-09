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

    static final String APP_SERVER_DEV_URL = "https://4w9niylxfh.execute-api.ap-northeast-2.amazonaws.com/";
    static final String APP_SERVER_PRD_URL = "https://8hl68sa8oc.execute-api.ap-northeast-2.amazonaws.com/";

    static final String APP_SERVER_DEV_API_KEY = "T29BotsPOE7WKnq9G0jEqaDvWTk0eY1T60Pf9d8W";
    static final String APP_SERVER_PRD_API_KEY = "T29BotsPOE7WKnq9G0jEqaDvWTk0eY1T60Pf9d8W";

    static final String PAYMENT_SERVICE_DEV_URL = "https://tm49i5w5x2.execute-api.ap-northeast-2.amazonaws.com/";
    static final String PAYMENT_SERVICE_PRD_URL = "https://ygipa8jw1c.execute-api.ap-northeast-2.amazonaws.com/";

    static final String KAKAO_PAY_TEST_CID = "TC0ONETIME";

    static final HttpLoggingInterceptor.Level LOGGER_LEVEL_BODY = HttpLoggingInterceptor.Level.BODY;
    static final HttpLoggingInterceptor.Level LOGGER_LEVEL_NONE = HttpLoggingInterceptor.Level.NONE;
}
