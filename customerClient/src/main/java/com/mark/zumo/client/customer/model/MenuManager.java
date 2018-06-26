/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.core.repository.MenuRepository;
import com.mark.zumo.client.core.util.DebugUtil;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuManager {
    INSTANCE;

    private MenuRepository menuRepository;

    private P2pClient p2pClient;

    MenuManager() {
        menuRepository = MenuRepository.INSTANCE;
    }

    public Observable<List<Menu>> acquireMenuItem(String storeUuid) {
        return menuRepository.getMenuItemsOfStore(storeUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> getMenuFromDisk(String uuid) {
        return menuRepository.getMenuFromDisk(uuid)
                .subscribeOn(Schedulers.io());
    }

    private Single<Store> currentStore() {
        //TODO: impl
        return Single.fromCallable(DebugUtil::store);
    }

    public void clearClient() {
        if (p2pClient == null) {
            return;
        }

        p2pClient.stopDiscovery();
        p2pClient = null;
    }

    public Observable<GroupedObservable<String, MenuOption>> getMenuOptionList(String menuUuid) {
        return menuRepository.getMenuOptionGroupByMenu(menuUuid)
                .subscribeOn(Schedulers.computation());
    }

    public Maybe<MenuOption> getMenuOptionFromDisk(String menuOptionUuid) {
        return menuRepository.getMenuOptionFromDisk(menuOptionUuid)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        return menuRepository.getMenuOptionList(menuOptionUuidList)
                .subscribeOn(Schedulers.io());
    }
}
