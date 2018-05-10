package com.mark.zumo.client.customer;

import android.app.Application;

import com.kakao.auth.KakaoSDK;
import com.mark.zumo.client.core.signup.kakao.KakaoSdkAdapter;
import com.mark.zumo.client.core.util.context.ContextInjector;

/**
 * Created by mark on 18. 5. 7.
 */

public class CustomerClientApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextInjector.inject(this);
        KakaoSDK.init(new KakaoSdkAdapter(this));
    }
}
