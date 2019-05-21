/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.database.AppDatabaseProvider;
import com.mark.zumo.client.core.database.dao.DiskRepository;
import com.mark.zumo.client.core.database.entity.MenuCategory;

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

    CategoryRepository() {
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    private NetworkRepository networkRepository() {
        return AppServerServiceProvider.INSTANCE.networkRepository();
    }

    public Observable<List<MenuCategory>> getMenuCategoryList(final String storeUuid) {
        Maybe<List<MenuCategory>> menuCategoryListDB = diskRepository.getMenuCategoryList(storeUuid);
        Maybe<List<MenuCategory>> menuCategoryListApi = networkRepository().getMenuCategoryListByStoreUuid(storeUuid)
                .doOnSuccess(categoryList -> diskRepository.deleteCategoriesOfStore(storeUuid))
                .doOnSuccess(diskRepository::insertMenuCategoryList);

        return Maybe.merge(menuCategoryListDB, menuCategoryListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<MenuCategory> createMenuCategory(final MenuCategory menuCategory) {
        return networkRepository().createMenuCategory(menuCategory)
                .doOnSuccess(diskRepository::insertMenuCategory);
    }

    public Maybe<MenuCategory> updateMenuCategory(final MenuCategory menuCategory) {
        return networkRepository().updateMenuCategory(menuCategory.uuid, menuCategory)
                .doOnSuccess(diskRepository::insertMenuCategory);
    }

    public Maybe<List<MenuCategory>> updateMenuCategory(final List<MenuCategory> menuCategoryList) {
        return networkRepository().updateMenuCategoryList(menuCategoryList)
                .doOnSuccess(diskRepository::insertMenuCategoryList);
    }

    public Maybe<MenuCategory> getMenuCategoryFromApi(final String categoryUuid) {
        return networkRepository().getMenuCategory(categoryUuid)
                .doOnSuccess(diskRepository::insertMenuCategory);
    }

    public Maybe<MenuCategory> getMenuCategoryFromDisk(final String categoryUuid) {
        return diskRepository.getMenuCategory(categoryUuid);
    }

    public Maybe<List<MenuCategory>> deleteCategories(final List<MenuCategory> menuCategoryList) {
        return Observable.fromIterable(menuCategoryList)
                .map(menuCategory -> menuCategory.uuid)
                .flatMapMaybe(networkRepository()::deleteCategory)
                .toList().toMaybe();
    }

    public Maybe<MenuCategory> deleteCategory(final String categoryUuid) {
        return networkRepository().deleteCategory(categoryUuid)
                .doOnSuccess(diskRepository::deleteMenuCategory);
    }
}
