package com.mark.zumo.server.store.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.signup.SessionCallbackFactory;

/**
 * Created by mark on 18. 5. 7.
 */

public class SignUpViewModel extends AndroidViewModel {

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        SessionCallbackFactory.create(SessionCallbackFactory.KAKAO);
    }
}
