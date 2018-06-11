/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.util.ListComparator;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuRepository {

    INSTANCE;

    private static final String TAG = "MenuRepository";

    private DiskRepository diskRepository;
    private NetworkRepository networkRepository;

    MenuRepository() {
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
    }

    private void onErrorOccurred(Throwable throwable) {
        Log.e(TAG, "onErrorOccurred: ", throwable);
    }

    public Observable<List<Menu>> getMenuItemsOfStore(Store store) {
        String storeUuid = store.uuid;

        Maybe<List<Menu>> menuListDB = diskRepository.getMenuList(storeUuid);
        Maybe<List<Menu>> menuListApi = networkRepository.getMenuList(storeUuid)
                .doOnSuccess(diskRepository::insertMenuList);

        return Maybe.merge(menuListDB, menuListApi)
                .toObservable()
                .doOnError(this::onErrorOccurred)
                .distinctUntilChanged(new ListComparator<>());
    }

    private Observable<List<MenuOption>> getMenuOptionsOfMenu(String menuUuid) {
        Maybe<List<MenuOption>> menuOptionListDB = diskRepository.getMenuOptionListByMenuUuid(menuUuid);
        Maybe<List<MenuOption>> menuOptionListApi = networkRepository.getMenuOptionList(menuUuid)
                .doOnSuccess(diskRepository::insertMenuOptionList);

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .doOnError(this::onErrorOccurred)
                .distinctUntilChanged(new ListComparator<>());
    }

    public Observable<GroupedObservable<String, MenuOption>> getMenuOptionGroupByMenu(String menuUuid) {
        return getMenuOptionsOfMenu(menuUuid)
                .flatMap(Observable::fromIterable)
                .groupBy(menuOption -> menuOption.name);
    }

    public Maybe<Menu> getMenuFromDisk(final String uuid) {
        return diskRepository.getMenu(uuid);
    }

    public Maybe<MenuOption> getMenuOptionFromDisk(final String menuOptionUuid) {
        return diskRepository.getMenuOption(menuOptionUuid);
    }

    public Observable<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        Maybe<List<MenuOption>> menuOptionListDB = Observable.fromIterable(menuOptionUuidList)
                .flatMapMaybe(diskRepository::getMenuOption)
                .toList().toMaybe();

        Maybe<List<MenuOption>> menuOptionListApi = networkRepository.getMenuOptionList(menuOptionUuidList)
                .doOnSuccess(diskRepository::insertMenuOptionList);

        return Maybe.merge(menuOptionListDB, menuOptionListApi)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .distinctUntilChanged(new ListComparator<>());
    }
}
