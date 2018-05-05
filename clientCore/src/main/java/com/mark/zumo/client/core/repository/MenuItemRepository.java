package com.mark.zumo.client.core.repository;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.MenuItemDao;
import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.core.entity.Store;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * Created by mark on 18. 4. 30.
 */

public class MenuItemRepository {

    private volatile static MenuItemRepository instance;

    private AppDatabase database;

    private MenuItemRepository(Context context) {
        database = AppDatabaseProvider.getDatabase(context);
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
        return Single.fromCallable(() -> {

            List<MenuItem> menuItemList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                MenuItem menuItem = new MenuItem((long) i, "TESTTESTTESTTESTTEST".getBytes(), store.id, 100, 0, 0);
                menuItemList.add(menuItem);
            }
            return menuItemList;
        });
    }
}
