/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.mark.zumo.client.core.app.BuildConfig;
import com.mark.zumo.client.core.util.context.ContextHolder;

/**
 * Created by mark on 18. 4. 30.
 */

public enum AppDatabaseProvider {
    INSTANCE;

    public static final String DB_NAME = "com.mark.zumun.client";

    public final DiskRepository diskRepository;
    public final MenuDao menuDao;
    public final MenuOptionDao menuOptionDao;
    public final MenuOrderDao menuOrderDao;
    public final StoreDao storeDao;
    public final StoreSessionDao storeSessionDao;
    public final StoreUserSessionDao storeUserSessionDao;

    AppDatabaseProvider() {
        AppDatabase appDatabase = buildDatabase(ContextHolder.getContext());

        diskRepository = appDatabase.diskRepository();
        menuDao = appDatabase.menuDao();
        menuOptionDao = appDatabase.menuOptionDao();
        menuOrderDao = appDatabase.menuOrderDao();
        storeDao = appDatabase.storeDao();
        storeSessionDao = appDatabase.storeSessionDao();
        storeUserSessionDao = appDatabase.storeUserSessionDao();
    }

    private static AppDatabase buildDatabase(Context context) {
        switch (BuildConfig.BUILD_TYPE) {
            case DEBUG:
//                return Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
            case RELEASE:
                return Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
            default:
                throw new UnsupportedOperationException();
        }
    }
}
