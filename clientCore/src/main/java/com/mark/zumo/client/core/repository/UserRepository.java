package com.mark.zumo.client.core.repository;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;

/**
 * Created by mark on 18. 4. 30.
 */

public class UserRepository {

    private volatile static UserRepository instance;


    private UserRepository(Context context) {
    }

    public static UserRepository from(Context context) {
        if (instance == null) {
            synchronized (UserRepository.class) {
                if (instance == null) instance = new UserRepository(context);
            }
        }
        return instance;
    }

    private NetworkRepository service() {
        return AppServerServiceProvider.INSTANCE.networkRepository;
    }
}
