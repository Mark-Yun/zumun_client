package com.mark.zumo.client.core.repository;

import android.content.Context;
import android.location.Location;

import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.StoreDao;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.DebugUtil;

import java.util.List;

import io.reactivex.Observable;

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

    public Observable<List<Store>> nearByStore(Location location) {
        return Observable.create(e -> {
            e.onNext(DebugUtil.storeList());
        });
    }

    public Observable<List<Store>> latestVisitStore() {
        return Observable.create(e -> {
            e.onNext(DebugUtil.storeList());
        });
    }
}
