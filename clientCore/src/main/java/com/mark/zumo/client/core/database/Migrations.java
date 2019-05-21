package com.mark.zumo.client.core.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

/**
 * Created by mark on 19. 5. 21.
 */
public class Migrations {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull final SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `paired_bluetooth_device` " +
                            "(`address` TEXT NOT NULL, `name` TEXT, `paired_date` INTEGER NOT NULL, PRIMARY KEY(`address`))"
            );
        }
    };
}
