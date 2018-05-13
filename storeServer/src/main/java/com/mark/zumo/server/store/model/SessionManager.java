package com.mark.zumo.server.store.model;

import io.reactivex.Single;

/**
 * Created by mark on 18. 4. 30.
 */

public enum SessionManager {
    INSTANCE;

    public Single<Boolean> isSessionValid() {
        return io.reactivex.Single.create(e -> {

            e.onSuccess(false);
        });
    }
}
