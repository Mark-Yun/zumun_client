package com.mark.zumo.client.core.repository;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.OrderDao;

/**
 * Created by mark on 18. 4. 30.
 */

public class OrderRepository {

    private volatile static OrderRepository instance;

    private AppDatabase database;

    private OrderRepository(Context context) {
        database = AppDatabaseProvider.getDatabase(context);
    }

    public static OrderRepository from(Context context) {
        if (instance == null) {
            synchronized (OrderRepository.class) {
                if (instance == null) instance = new OrderRepository(context);
            }
        }
        return instance;
    }

    private OrderDao orderDao() {
        return database.orderDao();
    }

    AppServerService appServerService() {
        return AppServerServiceProvider.INSTANCE.service;
    }
}
