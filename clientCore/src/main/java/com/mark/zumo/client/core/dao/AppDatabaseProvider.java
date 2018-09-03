/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.mark.zumo.client.core.util.context.ContextHolder;

/**
 * Created by mark on 18. 4. 30.
 */

public enum AppDatabaseProvider {
    INSTANCE;

    public static final String DB_NAME = "com.mark.zumun.client";
    public final DiskRepository diskRepository;

    AppDatabaseProvider() {
        diskRepository = buildDatabase(ContextHolder.getContext()).diskRepository();
    }

    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }
}
