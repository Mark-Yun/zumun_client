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

package com.mark.zumo.client.customer.app;

import android.app.Application;

import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.mark.zumo.client.core.app.AppErrorHandler;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.core.util.context.ContextInjector;

/**
 * Created by mark on 18. 5. 7.
 */

public class CustomerClientApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
    }

    private void initApplication() {
        ContextInjector.inject(this);
        AppErrorHandler.setup();
        KakaoSDK.init(new KakaoSdkAdapter());
    }

    private static class KakaoSdkAdapter extends KakaoAdapter {
        @Override
        public IApplicationConfig getApplicationConfig() {
            return ContextHolder::getContext;
        }
    }
}
