/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.os.Bundle;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.util.BundleUtils;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by mark on 18. 8. 5.
 */
public class CategoryRepository {

    private static final String TAG = "CategoryRepository";
    private static Bundle session;
    private static CategoryRepository sInstance;
    private final DiskRepository diskRepository;
    private final NetworkRepository networkRepository;

    private CategoryRepository(final Bundle session) {
        networkRepository = AppServerServiceProvider.INSTANCE.buildNetworkRepository(session);
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
        CategoryRepository.session = session;
    }

    public static CategoryRepository getInstance(Bundle session) {
        if (sInstance == null || !BundleUtils.equalsBundles(CategoryRepository.session, session)) {
            synchronized (CategoryRepository.class) {
                if (sInstance == null) {
                    sInstance = new CategoryRepository(session);
                }
            }
        }

        return sInstance;
    }

    public Observable<List<MenuCategory>> getMenuCategoryList(final String storeUuid) {
        Maybe<List<MenuCategory>> menuCategoryListDB = diskRepository.getMenuCategoryList(storeUuid);
        Maybe<List<MenuCategory>> menuCategoryListApi = networkRepository.getMenuCategoryListByStoreUuid(storeUuid)
                .doOnSuccess(categoryList -> diskRepository.deleteCategoriesOfStore(storeUuid))
                .doOnSuccess(diskRepository::insertMenuCategoryList);

        return Maybe.merge(menuCategoryListDB, menuCategoryListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<MenuCategory> createMenuCategory(final MenuCategory menuCategory) {
        return networkRepository.createMenuCategory(menuCategory)
                .doOnSuccess(diskRepository::insertMenuCategory);
    }

    public Maybe<MenuCategory> updateMenuCategory(final MenuCategory menuCategory) {
        return networkRepository.updateMenuCategory(menuCategory.uuid, menuCategory)
                .doOnSuccess(diskRepository::insertMenuCategory);
    }

    public Maybe<List<MenuCategory>> updateMenuCategory(final List<MenuCategory> menuCategoryList) {
        return networkRepository.updateMenuCategoryList(menuCategoryList)
                .doOnSuccess(diskRepository::insertMenuCategoryList);
    }

    public Maybe<MenuCategory> getMenuCategoryFromApi(final String categoryUuid) {
        return networkRepository.getMenuCategory(categoryUuid)
                .doOnSuccess(diskRepository::insertMenuCategory);
    }

    public Maybe<MenuCategory> getMenuCategoryFromDisk(final String categoryUuid) {
        return diskRepository.getMenuCategory(categoryUuid);
    }

    public Maybe<List<MenuCategory>> deleteCategories(final List<MenuCategory> menuCategoryList) {
        return Observable.fromIterable(menuCategoryList)
                .map(menuCategory -> menuCategory.uuid)
                .flatMapMaybe(networkRepository::deleteCategory)
                .toList().toMaybe();
    }

    public Maybe<MenuCategory> deleteCategory(final String categoryUuid) {
        return networkRepository.deleteCategory(categoryUuid)
                .doOnSuccess(diskRepository::deleteMenuCategory);
    }
}
