package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;

/**
 * Created by mark on 18. 4. 30.
 */

public enum OrderRepository {
    INSTANCE;

    OrderRepository() {
    }

    NetworkRepository appServerService() {
        return AppServerServiceProvider.INSTANCE.networkRepository;
    }
}
