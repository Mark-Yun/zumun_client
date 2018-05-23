package com.mark.zumo.client.core.repository;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.MenuItemDao;
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.Store;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mark on 18. 4. 30.
 */

public class MenuItemRepository {

    public static final String TAG = "MenuItemRepository";
    private volatile static MenuItemRepository instance;

    private AppDatabase database;
    private MenuItemDao menuItemDao;
    private Context context;
    private AppServerService appServerService;

    private MenuItemRepository(Context context) {
        menuItemDao = AppDatabaseProvider.getDatabase(context).menuItemDao();
        appServerService = AppServerServiceProvider.INSTANCE.service;
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

    public Observable<List<Menu>> getMenuItemsOfStore(Store store) {
        return Observable.create(e -> {
            String storeUuid = store.uuid;
            menuItemDao.findByStoreUuid(storeUuid).subscribe(e::onNext);
            appServerService.getMenuItemList(storeUuid)
                    .doOnSuccess(menuList -> menuItemDao.insertAll(menuList.toArray(new Menu[]{})))
                    .subscribe(e::onNext);
        });
    }
}
