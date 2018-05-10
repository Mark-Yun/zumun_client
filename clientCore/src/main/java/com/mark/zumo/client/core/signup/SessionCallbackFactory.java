package com.mark.zumo.client.core.signup;

import com.kakao.auth.Session;
import com.mark.zumo.client.core.signup.kakao.KakaoSessionCallback;

/**
 * Created by mark on 18. 5. 7.
 */

public class SessionCallbackFactory {

    public static final int KAKAO = 0;

    public static void create(int type, SessionCallback sessionCallback) {
        switch (type) {
            case KAKAO:
                KakaoSessionCallback kakaoSessionCallback = new KakaoSessionCallback(sessionCallback);
                Session.getCurrentSession().addCallback(kakaoSessionCallback);
                break;

            default:
                throw new UnsupportedOperationException();
        }
    }
}
