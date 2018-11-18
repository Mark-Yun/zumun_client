/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.os.Bundle;
import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOptionDetail;
import com.mark.zumo.client.core.util.BundleUtils;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by mark on 18. 4. 30.
 */

public class MenuRepository {

    private static final String TAG = "MenuRepository";

    private static Bundle session;
    private static MenuRepository sInstance;

    private final DiskRepository diskRepository;
    private final NetworkRepository networkRepository;

    private MenuRepository(final Bundle session) {
        networkRepository = AppServerServiceProvider.INSTANCE.buildNetworkRepository(session);
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
        MenuRepository.session = session;
    }

    public static MenuRepository getInstance(Bundle session) {
        if (sInstance == null || !BundleUtils.equalsBundles(MenuRepository.session, session)) {
            synchronized (MenuRepository.class) {
                if (sInstance == null) {
                    sInstance = new MenuRepository(session);
                }
            }
        }

        return sInstance;
    }

    public Observable<List<Menu>> getMenuListOfStore(String storeUuid) {
        Maybe<List<Menu>> menuListDB = diskRepository.getMenuList(storeUuid);
        Maybe<List<Menu>> menuListApi = networkRepository.getMenuList(storeUuid)
                .doOnSuccess(diskRepository::insertMenuList);

        return Maybe.merge(menuListDB, menuListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<List<Menu>> getMenuItemsOfStoreFromDisk(String storeUuid) {
        return diskRepository.getMenuList(storeUuid);
    }

    public Observable<List<MenuOption>> getMenuOptionByMenuUuid(String menuUuid) {
        Maybe<List<MenuOption>> menuOptionListDB = diskRepository.getMenuOptionListByMenuUuid(menuUuid);
        Maybe<List<MenuOption>> menuOptionListApi = networkRepository.getMenuOptionListByMenuUuid(menuUuid)
                .doOnSuccess(diskRepository::insertMenuOptionList)
                .doOnSuccess(list -> Log.d(TAG, "getMenuOptionByMenuUuid: " + list));

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Observable<List<MenuOptionDetail>> getMenuOptionDetailListByMenuOptionUuid(String menuOptionUuid) {
        Maybe<List<MenuOptionDetail>> menuOptionListDB = diskRepository.getMenuOptionDetailListByMenuOptionUuid(menuOptionUuid);
        Maybe<List<MenuOptionDetail>> menuOptionListApi = networkRepository.getMenuOptionListByOptionUuid(menuOptionUuid)
                .doOnSuccess(diskRepository::insertMenuOptionDetailList);

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<Menu> getMenuFromDisk(final String uuid) {
        return diskRepository.getMenu(uuid);
    }

    public Observable<Menu> getMenu(final String uuid) {
        Maybe<Menu> menuDB = diskRepository.getMenu(uuid);
        Maybe<Menu> menuApi = networkRepository.getMenu(uuid)
                .doOnSuccess(diskRepository::insertMenu);

        return Maybe.merge(menuDB, menuApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<MenuOption> getMenuOptionFromDisk(final String menuOptionUuid) {
        return diskRepository.getMenuOption(menuOptionUuid);
    }

    public Observable<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        Maybe<List<MenuOption>> menuOptionListDB = Observable.fromIterable(menuOptionUuidList)
                .flatMapMaybe(diskRepository::getMenuOption)
                .toList().toMaybe();

        Maybe<List<MenuOption>> menuOptionListApi = Observable.fromIterable(menuOptionUuidList)
                .flatMapMaybe(networkRepository::getMenuOptionList)
                .toList()
                .doOnSuccess(diskRepository::insertMenuOptionList)
                .toMaybe();

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Observable<List<MenuOption>> getMenuOptionListByStoreUuid(String storeUuid) {
        Maybe<List<MenuOption>> menuOptionListDB = diskRepository.getMenuOptionListByStoreUuid(storeUuid);
        Maybe<List<MenuOption>> menuOptionListApi = networkRepository.getMenuOptionListByStoreUuid(storeUuid)
                .doOnSuccess(diskRepository::insertMenuOptionList);

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<Menu> updateMenu(final Menu menu) {
        return networkRepository.updateMenu(menu.uuid, menu)
                .doOnSuccess(diskRepository::insertMenu);
    }

    public Maybe<Menu> updateCategoryInMenu(final String menuUuid, final MenuCategory menuCategory) {
        return networkRepository.updateCategoryInMenu(menuUuid, menuCategory)
                .doOnSuccess(diskRepository::insertMenu);
    }
}
