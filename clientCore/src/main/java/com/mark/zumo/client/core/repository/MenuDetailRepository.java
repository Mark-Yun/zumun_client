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

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by mark on 18. 8. 5.
 */
public enum MenuDetailRepository {
    INSTANCE;

    private NetworkRepository networkRepository;
    private DiskRepository diskRepository;

    MenuDetailRepository() {
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    public Observable<List<MenuDetail>> getMenuDetailByStoreUuid(String storeUuid) {
        Maybe<List<MenuDetail>> menuDetailListDB = diskRepository.getMenuDetailByStoreUuid(storeUuid);
        Maybe<List<MenuDetail>> menuDetailListApi = networkRepository.getMenuDetailByStoreUuid(storeUuid)
                .doOnSuccess(diskRepository::insertMenuDetailList);
        return Maybe.merge(menuDetailListDB, menuDetailListApi)
                .toObservable();
    }
}
