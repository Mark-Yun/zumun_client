package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Room;
import android.content.Context;

/**
 * Created by mark on 18. 4. 30.
 */

public class AppDatabaseProvider {

    private static final Object appDatabaseLock = new Object();

    private volatile static AppDatabase appDatabase;

    public static AppDatabase getDatabase(Context context) {
        if (appDatabase == null) {
            synchronized (appDatabaseLock) {
                if (appDatabase == null) {
                    appDatabase = buildDatabase(context);
                }
            }
        }

        return appDatabase;
    }

    private static AppDatabase buildDatabase(Context context) {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
    }
}
