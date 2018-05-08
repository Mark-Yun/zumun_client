package com.mark.zumo.client.core.repository;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.CustomerUserSessionDao;
import com.mark.zumo.client.core.dao.StoreSessionDao;

import io.reactivex.Single;

/**
 * Created by mark on 18. 4. 30.
 */

public class SessionRepository {

    private volatile static SessionRepository instance;

    private AppDatabase database;

    private SessionRepository(Context context) {
        database = AppDatabaseProvider.getDatabase(context);
    }

    public static SessionRepository from(Context context) {
        if (instance == null) {
            synchronized (SessionRepository.class) {
                if (instance == null) instance = new SessionRepository(context);
            }
        }
        return instance;
    }

    AppServerService appServerService() {
        return AppServerServiceProvider.INSTANCE.service;
    }

    private CustomerUserSessionDao customerUserSessionDao() {
        return database.customerUserSessionDao();
    }

    private StoreSessionDao storeSessionDao() {
        return database.storeSessionDao();
    }

    public Single<String> requestSessionId(String id, String password) {
        return Single.create(e -> {

        });
    }
}
