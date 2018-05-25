package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.mark.zumo.client.core.util.context.ContextHolder;

/**
 * Created by mark on 18. 4. 30.
 */

public enum AppDatabaseProvider {
    INSTANCE;

    public final AppDatabase appDatabase;

    AppDatabaseProvider() {
        appDatabase = buildDatabase(ContextHolder.getContext());
    }

    private static AppDatabase buildDatabase(Context context) {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
    }
}
