/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.util.Log;

import com.mark.zumo.client.core.appserver.request.RequestUpdateCategoriesOfMenu;
import com.mark.zumo.client.core.appserver.request.RequestUpdateMenusOfCategory;
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.p2p.packet.CombinedResult;
import com.mark.zumo.client.core.repository.CategoryRepository;
import com.mark.zumo.client.core.repository.MenuDetailRepository;
import com.mark.zumo.client.core.repository.MenuRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuManager {

    INSTANCE;

    private final static String TAG = "MenuManager";

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final MenuDetailRepository menuDetailRepository;

    MenuManager() {
        menuRepository = MenuRepository.INSTANCE;
        categoryRepository = CategoryRepository.INSTANCE;
        menuDetailRepository = MenuDetailRepository.INSTANCE;
    }

    public Observable<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        return menuRepository.getMenuOptionList(menuOptionUuidList)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Menu>> getMenuList(String storeUuid) {
        return menuRepository.getMenuListOfStore(storeUuid)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuCategory>> getMenuCategoryList(String storeUuid) {
        return categoryRepository.getMenuCategoryList(storeUuid)
                .doOnNext(categoryList -> Collections.sort(categoryList, (c1, c2) -> c2.seqNum - c1.seqNum))
                .subscribeOn(Schedulers.io());
    }


    public Maybe<Map<String, List<Menu>>> getMenuListByCategory(String storeUuid) {
        return menuRepository.getMenuListOfStore(storeUuid)
                .filter(menuList -> menuList.size() > 0)
                .map(menuList -> menuList.get(0).storeUuid)
                .flatMap(menuDetailRepository::getMenuDetailListOfStore)
                .flatMapSingle(groupedObservable ->
                        Single.zip(
                                Single.just(groupedObservable.getKey()),
                                groupedObservable.sorted((d1, d2) -> d2.menuSeqNum - d1.menuSeqNum)
                                        .map(menuDetail -> menuDetail.menuUuid)
                                        .flatMapMaybe(this::getMenuFromDisk)
                                        .toList(),
                                CombinedResult::new
                        )
                ).toMap(combinedResult -> combinedResult.t, combinedResult -> combinedResult.r)
                .toMaybe()
                .subscribeOn(Schedulers.computation());
    }

    public Maybe<List<Menu>> unCategorizedMenu(String storeUuid) {
        return menuDetailRepository.getMenuDetailListFromDisk(storeUuid)
                .flatMapObservable(Observable::fromIterable)
                .map(menuDetail -> menuDetail.menuUuid)
                .distinct()
                .toList()
                .flatMapMaybe(menuUuidList ->
                        menuRepository.getMenuItemsOfStoreFromDisk(storeUuid)
                                .flatMapObservable(Observable::fromIterable)
                                .filter(menu -> !menuUuidList.contains(menu.uuid))
                                .toList()
                                .toMaybe()
                ).subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> getMenuFromDisk(String menuUuid) {
        return menuRepository.getMenuFromDisk(menuUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuDetail>> getMenuDetailListFromDisk(String storeUuid, String menuUuid) {
        return menuDetailRepository.getMenuDetailListFromDiskByMenuUuid(storeUuid, menuUuid)
                .doOnSuccess(menuDetailList -> Log.d(TAG, "getMenuDetailListFromDisk: " + menuDetailList))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuDetail>> getMenuDetailListByCategoryUuid(String categoryUuid) {
        return menuDetailRepository.getMenuDetailListByCategoryUuid(categoryUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuCategory> getMenuCategoryFromDisk(String categoryUuid) {
        return categoryRepository.getMenuCategoryFromDisk(categoryUuid)
                .doOnSuccess(menuCategory -> Log.d(TAG, "getMenuCategoryFromDisk: " + menuCategory));
    }

    public Maybe<Menu> updateMenuName(final String menuUuid, final String menuName) {
        return menuRepository.getMenuFromDisk(menuUuid)
                .doOnSuccess(menu -> menu.name = menuName)
                .flatMap(menuRepository::updateMenu)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> updateMenuPrice(final String menuUuid, final int price) {
        return menuRepository.getMenuFromDisk(menuUuid)
                .doOnSuccess(menu -> menu.price = price)
                .flatMap(menuRepository::updateMenu)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> updateMenuImageUrl(final String menuUuid, final String imageUrl) {
        return menuRepository.getMenuFromDisk(menuUuid)
                .doOnSuccess(menu -> menu.imageUrl = imageUrl)
                .flatMap(menuRepository::updateMenu)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuCategory> createMenuCategory(final String name, final String storeUuid, final int seqNum) {
        return Maybe.fromCallable(() -> new MenuCategory(null, name, storeUuid, seqNum))
                .flatMap(categoryRepository::createMenuCategory)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuCategory>> updateMenuCategoryList(final List<MenuCategory> menuCategoryList) {
        return categoryRepository.updateMenuCategory(menuCategoryList)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuCategory> updateMenuCategoryName(final MenuCategory menuCategory, final String name) {
        MenuCategory newCategory = new MenuCategory(menuCategory.uuid, name, menuCategory.storeUuid, menuCategory.seqNum);
        return categoryRepository.updateMenuCategory(newCategory)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuCategory>> updateCategoriesOfMenu(final String storeUuid,
                                                            final String menuUuid,
                                                            final Set<String> categoryUuidSet) {
        RequestUpdateCategoriesOfMenu requestUpdateCategoriesOfMenu = new RequestUpdateCategoriesOfMenu(storeUuid, categoryUuidSet);
        return menuDetailRepository.updateCategoriesOfMenu(menuUuid, requestUpdateCategoriesOfMenu)
                .flatMapObservable(Observable::fromIterable)
                .map(menuDetail -> menuDetail.menuCategoryUuid)
                .flatMapMaybe(categoryRepository::getMenuCategoryFromDisk)
                .toList().toMaybe();
    }

    public Maybe<List<MenuDetail>> updateMenusOfCategory(final String storeUuid,
                                                         final String categoryUuid,
                                                         final List<String> menuUuidList) {
        RequestUpdateMenusOfCategory request = new RequestUpdateMenusOfCategory(storeUuid, menuUuidList);
        return menuDetailRepository.updateMenusOfCategory(categoryUuid, request)
                .subscribeOn(Schedulers.io());
    }
}
