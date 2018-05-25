package com.mark.zumo.client.core.repository;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;

/**
 * Created by mark on 18. 4. 30.
 */

public class OrderRepository {

    private volatile static OrderRepository instance;


    private OrderRepository(Context context) {
    }

    public static OrderRepository from(Context context) {
        if (instance == null) {
            synchronized (OrderRepository.class) {
                if (instance == null) instance = new OrderRepository(context);
            }
        }
        return instance;
    }

    NetworkRepository appServerService() {
        return AppServerServiceProvider.INSTANCE.networkRepository;
    }
}
