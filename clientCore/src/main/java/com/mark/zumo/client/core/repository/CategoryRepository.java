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
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.util.ListComparator;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by mark on 18. 8. 5.
 */
public enum CategoryRepository {

    INSTANCE;

    private static final String TAG = "CategoryRepository";

    private final DiskRepository diskRepository;
    private final NetworkRepository networkRepository;

    CategoryRepository() {
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
    }

    public Observable<List<MenuCategory>> getMenuCategoryList(final String storeUuid) {
        Maybe<List<MenuCategory>> menuCategoryListDB = diskRepository.getMenuCategoryList(storeUuid);
        Maybe<List<MenuCategory>> menuCategoryListApi = networkRepository.getMenuCategoryListByStoreUuid(storeUuid)
                .doOnSuccess(diskRepository::insertMenuCategoryList);

        return Maybe.merge(menuCategoryListDB, menuCategoryListApi)
                .toObservable()
                .distinctUntilChanged(new ListComparator<>());
    }

    public Maybe<MenuCategory> createMenuCategory(final MenuCategory menuCategory) {
        return networkRepository.createMenuCategory(menuCategory)
                .doOnSuccess(diskRepository::insertMenuCategory);
    }

    public Maybe<MenuCategory> updateMenuCategory(final MenuCategory menuCategory) {
        return networkRepository.updateMenuCategory(menuCategory.uuid, menuCategory)
                .doOnSuccess(diskRepository::insertMenuCategory);
    }

    public Maybe<MenuCategory> getMenuCategoryFromApi(final String categoryUuid) {
        return networkRepository.getMenuCategory(categoryUuid)
                .doOnSuccess(diskRepository::insertMenuCategory);
    }

    public Maybe<MenuCategory> getMenuCategoryFromDisk(final String categoryUuid) {
        return diskRepository.getMenuCategory(categoryUuid);
    }
}
