package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.session.CustomerUserSession;
import com.mark.zumo.client.core.entity.session.StoreSession;
import com.mark.zumo.client.core.entity.user.User;

/**
 * Created by mark on 18. 4. 30.
 */
@Database(
        entities = {
                MenuItem.class, MenuOrder.class, CustomerUserSession.class, Store.class, User.class, StoreSession.class
        }, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String NAME = "com.mark.zumo";

    public abstract MenuItemDao menuItemDao();
    public abstract MenuOrderDao orderDao();
    public abstract CustomerUserSessionDao customerUserSessionDao();
    public abstract StoreSessionDao storeSessionDao();
    public abstract StoreDao storeDao();
    public abstract UserDao userDao();
}
