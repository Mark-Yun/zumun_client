/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.util.DebugUtil;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum UserManager {
    INSTANCE;

    UserManager() {
    }

    public Observable<GuestUser> getCurrentUser() {
        //TODO: remove Test Data
        return Observable.just(DebugUtil.guestUser()).subscribeOn(Schedulers.io());
    }
}
