/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.database;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.mark.zumo.client.core.database.dao.DiskRepository;
import com.mark.zumo.client.core.database.dao.MenuDao;
import com.mark.zumo.client.core.database.dao.MenuOptionDao;
import com.mark.zumo.client.core.database.dao.MenuOrderDao;
import com.mark.zumo.client.core.database.dao.PairedBluetoothDeviceDao;
import com.mark.zumo.client.core.database.dao.StoreDao;
import com.mark.zumo.client.core.database.dao.StoreSessionDao;
import com.mark.zumo.client.core.database.dao.StoreUserSessionDao;
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
    public final PairedBluetoothDeviceDao pairedBluetoothDeviceDao;

    AppDatabaseProvider() {
        AppDatabase appDatabase = buildDatabase(ContextHolder.getContext());

        diskRepository = appDatabase.diskRepository();
        menuDao = appDatabase.menuDao();
        menuOptionDao = appDatabase.menuOptionDao();
        menuOrderDao = appDatabase.menuOrderDao();
        storeDao = appDatabase.storeDao();
        storeSessionDao = appDatabase.storeSessionDao();
        storeUserSessionDao = appDatabase.storeUserSessionDao();
        pairedBluetoothDeviceDao = appDatabase.pairedBluetoothDeviceDao();
    }

    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .addMigrations(Migrations.MIGRATION_1_2)
                .build();
    }
}
