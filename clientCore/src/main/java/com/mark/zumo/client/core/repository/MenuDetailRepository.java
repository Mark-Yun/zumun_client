/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.core.entity.util.ListComparator;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;

/**
 * Created by mark on 18. 8. 5.
 */
public enum MenuDetailRepository {
    INSTANCE;

    private final static String TAG = "MenuDetailRepository";

    private NetworkRepository networkRepository;
    private DiskRepository diskRepository;

    MenuDetailRepository() {
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    public Observable<GroupedObservable<String, MenuDetail>> getMenuDetailListOfStore(String storeUuid) {
        Maybe<List<MenuDetail>> menuListDB = diskRepository.getMenuDetailByStoreUuid(storeUuid);
        Maybe<List<MenuDetail>> menuListApi = networkRepository.getMenuDetailByStoreUuid(storeUuid)
                .doOnSuccess(diskRepository::insertMenuDetailList);

        return Maybe.merge(menuListDB, menuListApi)
                .toObservable()
                .distinctUntilChanged(new ListComparator<>())
                .flatMap(Observable::fromIterable)
                .groupBy(menuDetail -> menuDetail.menuCategoryUuid);
    }

    public Maybe<List<MenuDetail>> getMenuDetailListFromDisk(final String storeUuid) {
        return diskRepository.getMenuDetailByStoreUuid(storeUuid);
    }

    public Maybe<List<MenuDetail>> getMenuDetailListFromDiskByMenuUuid(final String storeUuid, final String menuUuid) {
        return diskRepository.getMenuDetailByStringMenuUuidFromDisk(storeUuid, menuUuid);
    }
}
