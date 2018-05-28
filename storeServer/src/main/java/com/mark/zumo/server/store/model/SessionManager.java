package com.mark.zumo.server.store.model;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum SessionManager {
    INSTANCE;

    public Single<Boolean> isSessionValid() {
        return Single.create((SingleOnSubscribe<Boolean>) e -> {

            e.onSuccess(false);
        }).subscribeOn(Schedulers.io());
    }
}
