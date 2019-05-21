/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.database.AppDatabaseProvider;
import com.mark.zumo.client.core.database.dao.DiskRepository;
import com.mark.zumo.client.core.database.dao.MenuDao;
import com.mark.zumo.client.core.database.dao.MenuOptionDao;
import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.core.database.entity.MenuCategory;
import com.mark.zumo.client.core.database.entity.MenuOption;
import com.mark.zumo.client.core.database.entity.MenuOptionCategory;
import com.mark.zumo.client.core.database.entity.MenuOptionDetail;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuRepository {
    INSTANCE;
    private static final String TAG = "MenuRepository";

    private final DiskRepository diskRepository;
    private final MenuDao menuDao;
    private final MenuOptionDao menuOptionDao;

    MenuRepository() {
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
        menuDao = AppDatabaseProvider.INSTANCE.menuDao;
        menuOptionDao = AppDatabaseProvider.INSTANCE.menuOptionDao;
    }

    private NetworkRepository networkRepository() {
        return AppServerServiceProvider.INSTANCE.networkRepository();
    }

    public Observable<List<Menu>> getMenuListOfStore(String storeUuid) {
        Maybe<List<Menu>> menuListDB = menuDao.getMenuList(storeUuid);
        Maybe<List<Menu>> menuListApi = networkRepository().getMenuList(storeUuid)
                .doOnSuccess(x -> menuDao.deleteMenuOfStore(storeUuid))
                .doOnSuccess(menuDao::insertMenuList);

        return Maybe.merge(menuListDB, menuListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<List<Menu>> getMenuItemsOfStoreFromDisk(String storeUuid) {
        return menuDao.getMenuList(storeUuid);
    }

    public Observable<List<MenuOption>> getMenuOptionListByMenuOptionCategoryUuid(String menuOptionCategoryUuid) {
        Maybe<List<MenuOption>> menuOptionListDB = menuOptionDao.getMenuOptionListByMenuOptionCategoryUuid(menuOptionCategoryUuid);
        Maybe<List<MenuOption>> menuOptionListApi = networkRepository().getMenuOptionListByMenuOptionCategoryUuid(menuOptionCategoryUuid)
                .doOnSuccess(diskRepository::insertMenuOptionList)
                .doOnSuccess(list -> Log.d(TAG, "getMenuOptionListByMenuOptionCategoryUuid: " + list));

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Observable<List<MenuOptionDetail>> getMenuOptionDetailListByMenuUuid(String menuUuid) {
        Maybe<List<MenuOptionDetail>> menuOptionListDB = diskRepository.getMenuOptionDetailListByMenuUuid(menuUuid);
        Maybe<List<MenuOptionDetail>> menuOptionListApi = networkRepository().getMenuOptionDetailListByMenuUuid(menuUuid)
                .doOnSuccess(x -> diskRepository.deleteMenuOptionDetailByMenuUuid(menuUuid))
                .doOnSuccess(diskRepository::insertMenuOptionDetailList);

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<MenuOptionDetail> getMenuOptionDetailFromDisk(final String menuOptionCategoryUuid,
                                                               final String menuUuid) {
        return diskRepository.getMenuOptionDetail(menuOptionCategoryUuid, menuUuid);
    }

    public Maybe<MenuOptionDetail> getMenuOptionDetailFromDisk(final String menuOptionDetailUuid) {
        return diskRepository.getMenuOptionDetail(menuOptionDetailUuid);
    }

    public Maybe<MenuOptionDetail> deleteMenuOptionDetail(final MenuOptionDetail menuOptionDetail) {
        return networkRepository().deleteMenuOptionDetail(menuOptionDetail.uuid)
                .doOnSuccess(diskRepository::deleteMenuOptionDetail)
                .map(x -> menuOptionDetail);
    }

    public Maybe<MenuOptionCategory> createMenuOptionCategory(MenuOptionCategory menuOptionCategory) {
        return networkRepository().createMenuOptionCategory(menuOptionCategory)
                .doOnSuccess(diskRepository::insertMenuOptionCategory);
    }

    public Maybe<List<MenuOptionDetail>> createMenuOptionDetailList(List<MenuOptionDetail> menuOptionDetailList) {
        return networkRepository().createMenuOptionDetailList(menuOptionDetailList)
                .doOnSuccess(diskRepository::insertMenuOptionDetailList)
                .doOnSuccess(list -> Log.d(TAG, "createMenuOptionDetailList: " + list));
    }

    public Maybe<Menu> createMenu(Menu menu) {
        return networkRepository().createMenu(menu)
                .doOnSuccess(menuDao::insertMenu);
    }

    public Maybe<List<MenuOption>> createMenuOptionList(List<MenuOption> menuOptionList) {
        return networkRepository().createMenuOptionList(menuOptionList)
                .doOnSuccess(diskRepository::insertMenuOptionList);
    }

    public Observable<GroupedObservable<String, MenuOptionDetail>> getMenuOptionDetailListByStoreUuid(String storeUuid) {
        Maybe<List<MenuOptionDetail>> menuOptionListDB = diskRepository.getMenuOptionDetailListByStoreUuid(storeUuid);
        Maybe<List<MenuOptionDetail>> menuOptionListApi = networkRepository().getMenuOptionDetailListByStoreUuid(storeUuid)
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
        return menuDao.getMenu(uuid);
    }

    public Maybe<Menu> getMenuFromApi(final String uuid) {
        return networkRepository().getMenu(uuid)
                .doOnSuccess(menuDao::insertMenu);
    }

    public Observable<Menu> getMenu(final String uuid) {
        Maybe<Menu> menuDB = menuDao.getMenu(uuid);
        Maybe<Menu> menuApi = networkRepository().getMenu(uuid)
                .doOnSuccess(menuDao::insertMenu);

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
                .flatMapMaybe(networkRepository()::getMenuOption)
                .toList()
                .doOnSuccess(diskRepository::insertMenuOptionList)
                .toMaybe();

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Observable<List<MenuOption>> getMenuOptionListByStoreUuid(String storeUuid) {
        Maybe<List<MenuOption>> menuOptionListDB = diskRepository.getMenuOptionListByStoreUuid(storeUuid);
        Maybe<List<MenuOption>> menuOptionListApi = networkRepository().getMenuOptionListByStoreUuid(storeUuid)
                .doOnSuccess(diskRepository::insertMenuOptionList);

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Observable<GroupedObservable<String, MenuOption>> getGroupedMenuOptionListByStoreUuid(String storeUuid) {
        Maybe<List<MenuOption>> menuOptionListDB = diskRepository.getMenuOptionListByStoreUuid(storeUuid);
        Maybe<List<MenuOption>> menuOptionListApi = networkRepository().getMenuOptionListByStoreUuid(storeUuid)
                .doOnSuccess(diskRepository::insertMenuOptionList);

        return Observable.merge(
                menuOptionListDB.flatMapObservable(Observable::fromIterable)
                        .groupBy(menuOption -> menuOption.menuOptionCategoryUuid),
                menuOptionListApi.flatMapObservable(Observable::fromIterable)
                        .groupBy(menuOption -> menuOption.menuOptionCategoryUuid))
                .distinctUntilChanged();
    }

    public Observable<MenuOptionCategory> getMenuOptionCategory(String menuOptionCategoryUuid) {
        Maybe<MenuOptionCategory> menuOptionCategoryDB = diskRepository.getMenuOptionCategory(menuOptionCategoryUuid);
        Maybe<MenuOptionCategory> menuOptionCategoryApi = networkRepository().getMenuOptionCategory(menuOptionCategoryUuid)
                .doOnSuccess(diskRepository::insertMenuOptionCategory);

        return Maybe.merge(menuOptionCategoryDB, menuOptionCategoryApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<MenuOptionCategory> getMenuOptionCategoryFromDisk(String menuOptionCategoryUuid) {
        return diskRepository.getMenuOptionCategory(menuOptionCategoryUuid);
    }

    public Observable<List<MenuOptionCategory>> getMenuOptionCategoryListByStoreUuid(String storeUuid) {
        Maybe<List<MenuOptionCategory>> menuOptionCategoryListApi = networkRepository().getMenuOptionCategoryListByStoreUuid(storeUuid)
                .doOnSuccess(x -> diskRepository.deleteMenuOptionCategoryListByStoreUuid(storeUuid))
                .doOnSuccess(diskRepository::insertMenuOptionCategoryList);
        Maybe<List<MenuOptionCategory>> menuOptionCategoryListDB = diskRepository.getMenuOptionCategoryListByStoreUuid(storeUuid);

        return Maybe.merge(menuOptionCategoryListDB, menuOptionCategoryListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<Menu> updateMenu(final Menu menu) {
        return networkRepository().updateMenu(menu.uuid, menu)
                .doOnSuccess(menuDao::insertMenu);
    }

    public Maybe<Menu> updateCategoryInMenu(final String menuUuid, final MenuCategory menuCategory) {
        return networkRepository().updateCategoryInMenu(menuUuid, menuCategory)
                .doOnSuccess(menuDao::insertMenu);
    }

    public Maybe<List<MenuOptionCategory>> deleteMenuOptionCategories(List<MenuOptionCategory> menuOptionCategoryList) {
        return Observable.fromIterable(menuOptionCategoryList)
                .map(menuOptionCategory -> menuOptionCategory.uuid)
                .flatMapMaybe(networkRepository()::deleteMenuOptionCategory)
                .toList().toMaybe()
                .flatMap(menuOptionCategories -> Observable.fromIterable(menuOptionCategories)
                        .map(menuOptionCategory -> menuOptionCategory.uuid)
                        .flatMapMaybe(diskRepository::getMenuOptionCategory)
                        .toList()
                        .toMaybe())
                .doOnSuccess(diskRepository::deleteMenuOptionCategoryList);
    }

    public Maybe<List<MenuOptionCategory>> updateMenuOptionCategories(List<MenuOptionCategory> menuOptionCategoryList) {
        return networkRepository().updateMenuOptionCategories(menuOptionCategoryList)
                .doOnSuccess(diskRepository::insertMenuOptionCategoryList);
    }

    public Maybe<List<MenuOptionDetail>> updateMenuOptionCategoriesOfMenu(String menuUuid, List<MenuOptionDetail> menuOptionDetailList) {
        return networkRepository().updateMenuOptionCategoriesOfMenu(menuUuid, menuOptionDetailList)
                .doOnSuccess(x -> diskRepository.deleteMenuOptionDetailOfMenu(menuUuid))
                .doOnSuccess(diskRepository::insertMenuOptionDetailList);
    }

    public Maybe<List<MenuOption>> deleteMenuOptions(List<MenuOption> menuOptionList) {
        return Observable.fromIterable(menuOptionList)
                .map(menuOption -> menuOption.uuid)
                .flatMapMaybe(networkRepository()::deleteMenuOption)
                .toList().toMaybe()
                .flatMap(menuOptions -> Observable.fromIterable(menuOptions)
                        .map(menuOption -> menuOption.uuid)
                        .flatMapMaybe(diskRepository::getMenuOption)
                        .toList()
                        .toMaybe())
                .doOnSuccess(diskRepository::deleteMenuOptionList);
    }

    public Maybe<List<MenuOption>> updateMenuOptions(List<MenuOption> menuOptionList) {
        return networkRepository().updateMenuOptions(menuOptionList)
                .doOnSuccess(diskRepository::insertMenuOptionList);
    }
}
