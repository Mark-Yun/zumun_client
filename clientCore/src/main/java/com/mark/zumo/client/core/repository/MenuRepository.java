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
import com.mark.zumo.client.core.entity.MenuOptionCategory;
import com.mark.zumo.client.core.entity.MenuOptionDetail;
import com.mark.zumo.client.core.util.BundleUtils;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;

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
                .doOnSuccess(x -> diskRepository.deleteMenuOfStore(storeUuid))
                .doOnSuccess(diskRepository::insertMenuList);

        return Maybe.merge(menuListDB, menuListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<List<Menu>> getMenuItemsOfStoreFromDisk(String storeUuid) {
        return diskRepository.getMenuList(storeUuid);
    }

    public Observable<List<MenuOption>> getMenuOptionListByMenuOptionCategoryUuid(String menuUuid) {
        Maybe<List<MenuOption>> menuOptionListDB = diskRepository.getMenuOptionListByMenuOptionCategoryUuid(menuUuid);
        Maybe<List<MenuOption>> menuOptionListApi = networkRepository.getMenuOptionListByMenuOptionCategoryUuid(menuUuid)
                .doOnSuccess(diskRepository::insertMenuOptionList)
                .doOnSuccess(list -> Log.d(TAG, "getMenuOptionListByMenuOptionCategoryUuid: " + list));

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Observable<List<MenuOptionDetail>> getMenuOptionDetailListByMenuOptionUuid(String menuOptionUuid) {
        Maybe<List<MenuOptionDetail>> menuOptionListDB = diskRepository.getMenuOptionDetailListByMenuOptionByStoreUuid(menuOptionUuid);
        Maybe<List<MenuOptionDetail>> menuOptionListApi = networkRepository.getMenuOptionDetailListByMenuOptionByStoreUuid(menuOptionUuid)
                .doOnSuccess(x -> diskRepository.deleteMenuOptionDetailOfMenuOptionCategory(menuOptionUuid))
                .doOnSuccess(diskRepository::insertMenuOptionDetailList);

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<MenuOptionDetail> getMenuOptionDetailFromDisk(final String menuOptionCategoryUuid,
                                                               final String menuUuid) {
        Log.d(TAG, "getMenuOptionDetailFromDisk: menuOptionCategoryUuid=" + menuOptionCategoryUuid + " menuUuid=" + menuUuid);
        return diskRepository.getMenuOptionDetail(menuOptionCategoryUuid, menuUuid)
                .doOnSuccess(menuOptionDetail -> Log.d(TAG, "getMenuOptionDetailFromDisk: " + menuOptionDetail));
    }

    public Maybe<MenuOptionDetail> deleteMenuOptionDetail(final MenuOptionDetail menuOptionDetail) {
        Log.d(TAG, "deleteMenuOptionDetail: " + menuOptionDetail);
        return networkRepository.deleteMenuOptionDetail(menuOptionDetail.id)
                .doOnSuccess(diskRepository::deleteMenuOptionDetail);
    }

    public Maybe<MenuOptionCategory> createMenuOptionCategory(MenuOptionCategory menuOptionCategory) {
        return networkRepository.createMenuOptionCategory(menuOptionCategory)
                .doOnSuccess(diskRepository::insertMenuOptionCategory);
    }

    public Maybe<List<MenuOptionDetail>> createMenuOptionDetailList(List<MenuOptionDetail> menuOptionDetailList) {
        return networkRepository.createMenuOptionDetailList(menuOptionDetailList)
                .doOnSuccess(diskRepository::insertMenuOptionDetailList);
    }

    public Maybe<List<MenuOption>> createMenuOptionList(List<MenuOption> menuOptionList) {
        return networkRepository.createMenuOptionList(menuOptionList)
                .doOnSuccess(diskRepository::insertMenuOptionList);
    }

    public Observable<GroupedObservable<String, MenuOptionDetail>> getMenuOptionDetailListByStoreUuid(String storeUuid) {
        Maybe<List<MenuOptionDetail>> menuOptionListDB = diskRepository.getMenuOptionDetailListByMenuOptionByStoreUuid(storeUuid);
        Maybe<List<MenuOptionDetail>> menuOptionListApi = networkRepository.getMenuOptionDetailListByMenuOptionByStoreUuid(storeUuid)
                .doOnSuccess(x -> diskRepository.deleteMenuOptionDetailOfStore(storeUuid))
                .doOnSuccess(diskRepository::insertMenuOptionDetailList)
                .doOnSuccess(menuOptionDetailList -> Log.d(TAG, "getMenuOptionDetailFromDisk: " + menuOptionDetailList));

        return Observable.merge(
                menuOptionListDB.flatMapObservable(Observable::fromIterable)
                        .groupBy(menuOptionDetail -> menuOptionDetail.menuOptionCategoryUuid),
                menuOptionListApi.flatMapObservable(Observable::fromIterable)
                        .groupBy(menuOptionDetail -> menuOptionDetail.menuOptionCategoryUuid)
        ).distinctUntilChanged();
    }

    public Maybe<Menu> getMenuFromDisk(final String uuid) {
        return diskRepository.getMenu(uuid);
    }

    public Maybe<Menu> getMenuFromApi(final String uuid) {
        return networkRepository.getMenu(uuid)
                .doOnSuccess(diskRepository::insertMenu);
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
                .flatMapMaybe(networkRepository::getMenuOption)
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

    public Observable<GroupedObservable<String, MenuOption>> getGroupedMenuOptionListByStoreUuid(String storeUuid) {
        Maybe<List<MenuOption>> menuOptionListDB = diskRepository.getMenuOptionListByStoreUuid(storeUuid);
        Maybe<List<MenuOption>> menuOptionListApi = networkRepository.getMenuOptionListByStoreUuid(storeUuid)
                .doOnSuccess(diskRepository::insertMenuOptionList);

        return Observable.merge(
                menuOptionListDB.flatMapObservable(Observable::fromIterable)
                        .groupBy(menuOption -> menuOption.menuOptionCategoryUuid),
                menuOptionListApi.flatMapObservable(Observable::fromIterable)
                        .groupBy(menuOption -> menuOption.menuOptionCategoryUuid))
                .distinctUntilChanged();
    }

    public Observable<List<MenuOptionCategory>> getMenuOptionCategoryListByStoreUuid(String storeUuid) {
        Maybe<List<MenuOptionCategory>> menuOptionCategoryListApi = networkRepository.getMenuOptionCategoryListByStoreUuid(storeUuid)
                .doOnSuccess(x -> diskRepository.deleteMenuOptionCategoryListByStoreUuid(storeUuid))
                .doOnSuccess(diskRepository::insertMenuOptionCategoryList);
        Maybe<List<MenuOptionCategory>> menuOptionCategoryListDB = diskRepository.getMenuOptionCategoryListByStoreUuid(storeUuid);

        return Maybe.merge(menuOptionCategoryListDB, menuOptionCategoryListApi)
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

    public Maybe<List<MenuOptionCategory>> deleteMenuOptionCategories(List<MenuOptionCategory> menuOptionCategoryList) {
        return Observable.fromIterable(menuOptionCategoryList)
                .map(menuOptionCategory -> menuOptionCategory.uuid)
                .flatMapMaybe(networkRepository::deleteMenuOptionCategory)
                .toList().toMaybe()
                .flatMap(menuOptionCategories -> Observable.fromIterable(menuOptionCategories)
                        .map(menuOptionCategory -> menuOptionCategory.uuid)
                        .flatMapMaybe(diskRepository::getMenuOptionCategory)
                        .toList()
                        .toMaybe())
                .doOnSuccess(diskRepository::deleteMenuOptionCategoryList);
    }

    public Maybe<List<MenuOptionCategory>> updateMenuOptionCategories(List<MenuOptionCategory> menuOptionCategoryList) {
        return networkRepository.updateMenuOptionCategories(menuOptionCategoryList)
                .doOnSuccess(diskRepository::insertMenuOptionCategoryList);
    }

    public Maybe<List<MenuOption>> deleteMenuOptions(List<MenuOption> menuOptionList) {
        return Observable.fromIterable(menuOptionList)
                .map(menuOption -> menuOption.uuid)
                .flatMapMaybe(networkRepository::deleteMenuOption)
                .toList().toMaybe()
                .flatMap(menuOptions -> Observable.fromIterable(menuOptions)
                        .map(menuOption -> menuOption.uuid)
                        .flatMapMaybe(diskRepository::getMenuOption)
                        .toList()
                        .toMaybe())
                .doOnSuccess(diskRepository::deleteMenuOptionList);
    }

    public Maybe<List<MenuOption>> updateMenuOptions(List<MenuOption> menuOptionList) {
        return networkRepository.updateMenuOptions(menuOptionList)
                .doOnSuccess(diskRepository::insertMenuOptionList);
    }
}
