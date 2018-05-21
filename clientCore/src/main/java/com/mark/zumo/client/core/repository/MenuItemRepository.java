package com.mark.zumo.client.core.repository;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.MenuItemDao;
import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.DebugUtil;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by mark on 18. 4. 30.
 */

public class MenuItemRepository {

    private volatile static MenuItemRepository instance;

    private AppDatabase database;
    private Context context;

    private MenuItemRepository(Context context) {
        database = AppDatabaseProvider.getDatabase(context);
        this.context = context;
    }

    public static MenuItemRepository from(Context context) {
        if (instance == null) {
            synchronized (MenuItemRepository.class) {
                if (instance == null) instance = new MenuItemRepository(context);
            }
        }
        return instance;
    }

    private MenuItemDao menuItemDao() {
        return database.menuItemDao();
    }

    private AppServerService appServerService() {
        return AppServerServiceProvider.INSTANCE.service;
    }

    public Single<List<MenuItem>> getMenuItemsOfStore(Store store) {
        //TODO: remove test data
        return Single.fromCallable(DebugUtil::menuItemList);
    }
}
