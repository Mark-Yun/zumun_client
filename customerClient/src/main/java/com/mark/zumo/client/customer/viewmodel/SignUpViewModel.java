package com.mark.zumo.client.customer.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.signup.SessionCallback;
import com.mark.zumo.client.core.signup.SessionCallbackFactory;
import com.mark.zumo.client.customer.model.SessionManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 7.
 */

public class SignUpViewModel extends AndroidViewModel {

    private SessionManager sessionManager;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        sessionManager = SessionManager.INSTANCE;
        SessionCallbackFactory.create(SessionCallbackFactory.KAKAO, sessionCallback());
    }

    @NonNull
    private SessionCallback sessionCallback() {
        return new SessionCallback() {
            @Override
            public void onSuccess(int resultCode) {

            }

            @Override
            public void onFailure(int resultCode) {

            }
        };
    }

    public LiveData<Boolean> isSessionValid() {
        MutableLiveData<Boolean> existSession = new MutableLiveData<>();

        sessionManager.isSessionValid()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(existSession::setValue);

        return existSession;
    }
}
