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
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.core.util.BundleUtils;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;

/**
 * Created by mark on 18. 8. 5.
 */
public class MenuDetailRepository {

    private final static String TAG = "MenuDetailRepository";

    private static Bundle session;
    private static MenuDetailRepository sInstance;
    private final DiskRepository diskRepository;
    private final NetworkRepository networkRepository;

    private MenuDetailRepository(final Bundle session) {
        networkRepository = AppServerServiceProvider.INSTANCE.buildNetworkRepository(session);
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
        MenuDetailRepository.session = session;
    }

    public static MenuDetailRepository getInstance(Bundle session) {
        if (sInstance == null || !BundleUtils.equalsBundles(MenuDetailRepository.session, session)) {
            synchronized (MenuDetailRepository.class) {
                if (sInstance == null) {
                    sInstance = new MenuDetailRepository(session);
                }
            }
        }

        return sInstance;
    }
    public Observable<GroupedObservable<String, MenuDetail>> getMenuDetailListOfStore(String storeUuid) {
        Maybe<List<MenuDetail>> menuListDB = diskRepository.getMenuDetailByStoreUuid(storeUuid);
        Maybe<List<MenuDetail>> menuListApi = networkRepository.getMenuDetailByStoreUuid(storeUuid)
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

    public Observable<List<MenuDetail>> getMenuDetailListByCategoryUuid(final String categoryUuid) {
        Maybe<List<MenuDetail>> menuDetailListDB = diskRepository.getMenuDetailByCategoryUuid(categoryUuid);
        Maybe<List<MenuDetail>> menuDetailListApi = networkRepository.getMenuDetailByCategoryUuid(categoryUuid)
                .doOnSuccess(x -> diskRepository.deleteMenuDetailListByCategoryUuid(categoryUuid))
                .doOnSuccess(diskRepository::insertMenuDetailList);
        return Maybe.merge(menuDetailListDB, menuDetailListApi)
                .toObservable();
    }

    public Maybe<List<MenuDetail>> getMenuDetailListFromDiskByMenuUuid(final String storeUuid, final String menuUuid) {
        return diskRepository.getMenuDetailByStringMenuUuidFromDisk(storeUuid, menuUuid);
    }

    public Maybe<List<MenuDetail>> updateCategoriesOfMenu(final String menuUuid,
                                                          final List<MenuDetail> menuDetailList) {
        return networkRepository.updateCategoriesOfMenu(menuUuid, menuDetailList)
                .doOnSuccess(result -> diskRepository.deleteMenuDetailListByMenuUuid(menuUuid))
                .doOnSuccess(diskRepository::insertMenuDetailList);
    }

    public Maybe<List<MenuDetail>> updateMenusOfCategory(final String categoryUuid,
                                                         final List<MenuDetail> menuDetailList) {
        return networkRepository.updateMenusOfCategory(categoryUuid, menuDetailList)
                .doOnSuccess(result -> diskRepository.deleteMenuDetailListByCategoryUuid(categoryUuid))
                .doOnSuccess(diskRepository::insertMenuDetailList);
    }
}
