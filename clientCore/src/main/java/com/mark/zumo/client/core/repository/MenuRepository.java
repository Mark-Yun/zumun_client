package com.mark.zumo.client.core.repository;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.MenuDao;
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.util.EntityComparator;
import com.mark.zumo.client.core.entity.util.ListComparator;
import com.mark.zumo.client.core.util.DebugUtil;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observables.GroupedObservable;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuRepository {
    INSTANCE;

    public static final String TAG = "MenuRepository";
    private volatile static MenuRepository instance;

    private AppDatabase database;
    private MenuDao menuDao;
    private Context context;
    private AppServerService appServerService;

    MenuRepository() {
        context = ContextHolder.getContext();
        menuDao = AppDatabaseProvider.getDatabase(context).menuItemDao();
        appServerService = AppServerServiceProvider.INSTANCE.service;
    }

    public Observable<List<Menu>> getMenuItemsOfStore(Store store) {
        return Observable.create((ObservableOnSubscribe<List<Menu>>) e -> {
            String storeUuid = store.uuid;
            menuDao.findByStoreUuid(storeUuid).subscribe(e::onNext);
            appServerService.getMenuItemList(storeUuid)
                    .doOnSuccess(menuList -> menuDao.insertAll(menuList.toArray(new Menu[]{})))
                    .subscribe(e::onNext);
        }).distinctUntilChanged(new ListComparator<>());
    }

    private Observable<List<MenuOption>> getMenuOptionsOfMenu(String menuUuid) {
        return Observable.create((ObservableOnSubscribe<List<MenuOption>>) e -> {
            //TODO: remove test data
            e.onNext(DebugUtil.menuOptionList(menuUuid));
            e.onComplete();
        }).distinctUntilChanged(new ListComparator<>());
    }

    private Observable<MenuOption> getMenuOptionFromList(List<MenuOption> menuOptionList) {
        return Observable.create(e -> {
            for (int i = 0; i < menuOptionList.size(); i++)
                e.onNext(menuOptionList.get(i));
            e.onComplete();
        });
    }

    public Observable<GroupedObservable<String, MenuOption>> getMenuOptionGroupByMenu(String menuUuid) {
        return getMenuOptionsOfMenu(menuUuid)
                .flatMap(this::getMenuOptionFromList)
                .groupBy(menuOption -> menuOption.name);
    }

    public Observable<Menu> getMenu(final String uuid) {
        return Observable.create((ObservableOnSubscribe<Menu>) e -> {
            menuDao.findByUuid(uuid).subscribe(e::onNext);
        }).distinctUntilChanged(new EntityComparator<>());
    }
}
