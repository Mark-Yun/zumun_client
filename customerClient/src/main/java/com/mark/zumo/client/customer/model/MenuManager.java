/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.core.repository.CategoryRepository;
import com.mark.zumo.client.core.repository.MenuDetailRepository;
import com.mark.zumo.client.core.repository.MenuRepository;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuManager {
    INSTANCE;

    private final MenuRepository menuRepository;
    private final MenuDetailRepository menuDetailRepository;
    private final CategoryRepository categoryRepository;

    private P2pClient p2pClient;

    MenuManager() {
        menuRepository = MenuRepository.INSTANCE;
        categoryRepository = CategoryRepository.INSTANCE;
        menuDetailRepository = MenuDetailRepository.INSTANCE;
    }

    public Maybe<Menu> getMenuFromDisk(String uuid) {
        return menuRepository.getMenuFromDisk(uuid)
                .subscribeOn(Schedulers.io());
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

    public Observable<GroupedObservable<String, MenuDetail>> getMenuListByCategory(String storeUuid) {
        return menuRepository.getMenuListOfStore(storeUuid)
                .concatMap(unused -> menuDetailRepository.getMenuDetailListOfStore(storeUuid))
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

    public Observable<List<MenuCategory>> getMenuCategoryList(String storeUuid) {
        return categoryRepository.getMenuCategoryList(storeUuid)
                .flatMapSingle(menuCategories ->
                        Observable.fromIterable(menuCategories)
                                .sorted((c1, c2) -> c2.seqNum - c1.seqNum)
                                .toList()
                ).subscribeOn(Schedulers.io());
    }
}
