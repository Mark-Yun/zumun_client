package com.mark.zumo.client.core.repository;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.StoreDao;

/**
 * Created by mark on 18. 4. 30.
 */

public class StoreRepository {

    private volatile static StoreRepository instance;

    private AppDatabase database;

    private StoreRepository(Context context) {
        database = AppDatabaseProvider.getDatabase(context);
    }

    public static StoreRepository from(Context context) {
        if (instance == null) {
            synchronized (StoreRepository.class) {
                if (instance == null) instance = new StoreRepository(context);
            }
        }
        return instance;
    }

    private StoreDao storeDao() {
        return database.storeDao();
    }

    private AppServerService appServerService() {
        return AppServerServiceProvider.INSTANCE.service;
    }
}
