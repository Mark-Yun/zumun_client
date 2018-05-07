package com.mark.zumo.client.customer;

import android.app.Application;

import com.kakao.auth.KakaoSDK;
import com.mark.zumo.client.core.signup.kakao.KakaoSdkAdapter;

/**
 * Created by mark on 18. 5. 7.
 */

public class CustomerClientApp extends Application {

    private static CustomerClientApp instance;

    public static CustomerClientApp getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        KakaoSDK.init(new KakaoSdkAdapter(this));
    }
}
