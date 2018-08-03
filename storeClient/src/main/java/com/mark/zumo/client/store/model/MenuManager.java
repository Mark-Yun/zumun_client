/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.repository.MenuRepository;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuManager {
    INSTANCE;

    private final MenuRepository menuRepository;

    MenuManager() {
        menuRepository = MenuRepository.INSTANCE;
    }

    public Observable<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        return menuRepository.getMenuOptionList(menuOptionUuidList)
                .subscribeOn(Schedulers.io());
    }


    public Observable<List<Menu>> getMenuList(String storeUuid) {
        return menuRepository.getMenuItemsOfStore(storeUuid)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuCategory>> getMenuCategoryList(String storeUuid) {
        return menuRepository.getMenuCategoryList(storeUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> getMenuFromDisk(String menuUuid) {
        return menuRepository.getMenuFromDisk(menuUuid)
                .subscribeOn(Schedulers.io());
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

    public Maybe<MenuCategory> createMenuCategory(String name, String storeUuid, int seqNum) {
        return Maybe.fromCallable(() -> new MenuCategory(null, name, storeUuid, seqNum))
                .flatMap(menuRepository::createMenuCategory)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuCategory>> updateMenuCategoryList(List<MenuCategory> menuCategoryList) {
        return Observable.fromIterable(menuCategoryList)
                .flatMapMaybe(menuRepository::updateMenuCategory)
                .toList().toMaybe()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuCategory> updateMenuCategoryName(MenuCategory menuCategory, String name) {
        MenuCategory newCategory = new MenuCategory(menuCategory.uuid, name, menuCategory.storeUuid, menuCategory.seqNum);
        return menuRepository.updateMenuCategory(newCategory)
                .subscribeOn(Schedulers.io());
    }
}
