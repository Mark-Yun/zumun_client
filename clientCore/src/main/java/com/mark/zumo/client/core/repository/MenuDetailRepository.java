/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.appserver.request.MenuCategoryUpdateRequest;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.MenuDetail;

import java.util.List;
import java.util.Set;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;

/**
 * Created by mark on 18. 8. 5.
 */
public enum MenuDetailRepository {
    INSTANCE;

    private final static String TAG = "MenuDetailRepository";

    private DiskRepository diskRepository;

    MenuDetailRepository() {
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    private NetworkRepository networkRepository() {
        return AppServerServiceProvider.INSTANCE.networkRepository;
    }

    public Observable<GroupedObservable<String, MenuDetail>> getMenuDetailListOfStore(String storeUuid) {
        Maybe<List<MenuDetail>> menuListDB = diskRepository.getMenuDetailByStoreUuid(storeUuid);
        Maybe<List<MenuDetail>> menuListApi = networkRepository().getMenuDetailByStoreUuid(storeUuid)
                .doOnSuccess(unused -> diskRepository.deleteMenuDetailListByStoreUuid(storeUuid))
                .doOnSuccess(diskRepository::insertMenuDetailList);

        return Observable.merge(
                menuListDB.flatMapObservable(Observable::fromIterable)
                        .groupBy(menuDetail -> menuDetail.menuCategoryUuid),
                menuListApi.flatMapObservable(Observable::fromIterable)
                        .groupBy(menuDetail -> menuDetail.menuCategoryUuid));
    }

    public Maybe<List<MenuDetail>> getMenuDetailListFromDisk(final String storeUuid) {
        return diskRepository.getMenuDetailByStoreUuid(storeUuid);
    }

    public Maybe<List<MenuDetail>> getMenuDetailListFromDiskByMenuUuid(final String storeUuid, final String menuUuid) {
        return diskRepository.getMenuDetailByStringMenuUuidFromDisk(storeUuid, menuUuid);
    }

    public Maybe<List<MenuDetail>> updateMenuCategory(final String menuUuid,
                                                      final String storeUuid,
                                                      final Set<String> categoryUuidList) {
        MenuCategoryUpdateRequest menuCategoryUpdateRequest = new MenuCategoryUpdateRequest(storeUuid, categoryUuidList);
        return networkRepository().updateMenuCategory(menuUuid, menuCategoryUpdateRequest)
                .doOnSuccess(menuDetailList -> {
                    if (menuDetailList.size() > 0) {
                        MenuDetail menuDetail = menuDetailList.get(0);
                        diskRepository.deleteMenuDetailListByMenuUuid(menuDetail.menuUuid);
                    }
                })
                .doOnSuccess(diskRepository::insertMenuDetailList);
    }
}
