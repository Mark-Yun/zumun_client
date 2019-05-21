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
import com.mark.zumo.client.core.database.entity.MenuDetail;

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

    private final DiskRepository diskRepository;

    MenuDetailRepository() {
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    private NetworkRepository networkRepository() {
        return AppServerServiceProvider.INSTANCE.networkRepository();
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
                        .groupBy(menuDetail -> menuDetail.menuCategoryUuid))
                .distinctUntilChanged();
    }

    public Maybe<List<MenuDetail>> createMenuDetailList(List<MenuDetail> menuDetailList) {
        return networkRepository().createMenuDetailList(menuDetailList)
                .doOnSuccess(diskRepository::insertMenuDetailList);
    }

    public Maybe<List<MenuDetail>> getMenuDetailListFromDisk(final String storeUuid) {
        return diskRepository.getMenuDetailByStoreUuid(storeUuid);
    }

    public Maybe<MenuDetail> getMenuDetailByCategoryUuidAndMenuUuidFromDisk(final String categoryUuid,
                                                                            final String menuUuid) {
        return diskRepository.getMenuDetailByCategoryUuidAndMenuUuid(categoryUuid, menuUuid);
    }

    public Maybe<List<MenuDetail>> removeMenuDetailList(final List<MenuDetail> menuDetailList) {
        return Observable.fromIterable(menuDetailList)
                .map(menuDetail -> menuDetail.uuid)
                .flatMapMaybe(menuDetail -> networkRepository().deleteMenuDetail(menuDetail)
                        .map(menuDetail1 -> menuDetail1.uuid)
                        .flatMap(diskRepository::getMenuDetail))
                .toList().toMaybe()
                .doOnSuccess(diskRepository::deleteMenuDetailList);
    }

    public Maybe<List<MenuDetail>> updateMenuDetailSequence(final String menuCategoryUuid,
                                                            final List<MenuDetail> menuDetailList) {
        return networkRepository().updateMenusOfCategory(menuCategoryUuid, menuDetailList)
                .doOnSuccess(diskRepository::insertMenuDetailList);
    }

    public Observable<List<MenuDetail>> getMenuDetailListByCategoryUuid(final String categoryUuid) {
        Maybe<List<MenuDetail>> menuDetailListDB = diskRepository.getMenuDetailByCategoryUuid(categoryUuid);
        Maybe<List<MenuDetail>> menuDetailListApi = networkRepository().getMenuDetailByCategoryUuid(categoryUuid)
                .doOnSuccess(x -> diskRepository.deleteMenuDetailListByCategoryUuid(categoryUuid))
                .doOnSuccess(diskRepository::insertMenuDetailList);
        return Maybe.merge(menuDetailListDB, menuDetailListApi)
                .toObservable()
                .distinctUntilChanged();
    }

    public Maybe<List<MenuDetail>> getMenuDetailListFromDiskByMenuUuid(final String storeUuid, final String menuUuid) {
        return diskRepository.getMenuDetailByStringMenuUuid(storeUuid, menuUuid);
    }

    public Maybe<List<MenuDetail>> updateCategoriesOfMenu(final String menuUuid,
                                                          final List<MenuDetail> menuDetailList) {
        return networkRepository().updateCategoriesOfMenu(menuUuid, menuDetailList)
                .doOnSuccess(result -> diskRepository.deleteMenuDetailListByMenuUuid(menuUuid))
                .doOnSuccess(diskRepository::insertMenuDetailList);
    }

    public Maybe<List<MenuDetail>> updateMenusOfCategory(final String categoryUuid,
                                                         final List<MenuDetail> menuDetailList) {
        return networkRepository().updateMenusOfCategory(categoryUuid, menuDetailList)
                .doOnSuccess(result -> diskRepository.deleteMenuDetailListByCategoryUuid(categoryUuid))
                .doOnSuccess(diskRepository::insertMenuDetailList);
    }
}
