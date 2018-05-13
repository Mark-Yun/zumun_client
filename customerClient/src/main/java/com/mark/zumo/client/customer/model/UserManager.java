package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.entity.user.GuestUser;

import io.reactivex.Observable;

/**
 * Created by mark on 18. 4. 30.
 */

public enum UserManager {
    INSTANCE;

    UserManager() {
    }

    public Observable<GuestUser> getCurrentUser() {
        //TODO: remove Test Data
        return Observable.just(new GuestUser(99, "test user", 0));
    }
}
