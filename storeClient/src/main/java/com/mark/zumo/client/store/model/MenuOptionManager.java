/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.support.annotation.NonNull;

import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.core.database.entity.MenuOption;
import com.mark.zumo.client.core.database.entity.MenuOptionCategory;
import com.mark.zumo.client.core.database.entity.MenuOptionDetail;
import com.mark.zumo.client.core.repository.MenuRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 12. 5.
 */
public enum MenuOptionManager {
    INSTANCE;

    private static final String TAG = "MenuOptionManager";

    private final MenuRepository menuRepository;

    MenuOptionManager() {
        menuRepository = MenuRepository.INSTANCE;
    }

    public Observable<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        return menuRepository.getMenuOptionList(menuOptionUuidList)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuOptionCategory>> getCombinedMenuOptionCategoryListByStoreUuid(String storeUuid) {
        return Observable.create((ObservableOnSubscribe<List<MenuOptionCategory>>) e -> {
            List<MenuOptionCategory> menuOptionCategoryList = new CopyOnWriteArrayList<>();
            Map<String, List<MenuOption>> menuOptionMap = new ConcurrentHashMap<>();
            List<Menu> menuList = new CopyOnWriteArrayList<>();
            Map<String, List<MenuOptionDetail>> menuOptionDetailMap = new ConcurrentHashMap<>();

            Set<Class> nextToken = new CopyOnWriteArraySet<>();
            Set<Class> completeToken = new CopyOnWriteArraySet<>();

            nextToken.add(MenuOptionCategory.class);
            completeToken.add(MenuOptionCategory.class);
            menuRepository.getMenuOptionCategoryListByStoreUuid(storeUuid)
                    .doOnNext(menuOptionCategories -> {
                        menuOptionCategoryList.clear();
                        menuOptionCategoryList.addAll(menuOptionCategories);
                        nextToken.remove(MenuOptionCategory.class);
                        if (nextToken.isEmpty()) {
                            List<MenuOptionCategory> combinedMenuOptionCategory = mapMenuOptionCategoryWithMenuOption(menuOptionCategoryList, menuOptionMap, menuList, menuOptionDetailMap);
                            if (!combinedMenuOptionCategory.isEmpty()) {
                                e.onNext(combinedMenuOptionCategory);
                            }
                        }
                    })
                    .doOnComplete(() -> {
                        completeToken.remove(MenuOptionCategory.class);
                        if (completeToken.isEmpty()) {
                            e.onComplete();
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .subscribe();

            nextToken.add(MenuOption.class);
            completeToken.add(MenuOption.class);
            menuRepository.getGroupedMenuOptionListByStoreUuid(storeUuid)
                    .subscribeOn(Schedulers.newThread())
                    .flatMapSingle(Observable::toList)
                    .toMap(menuOptionDetails -> menuOptionDetails.get(0).menuOptionCategoryUuid)
                    .doOnSuccess(menuOptions -> {
                        menuOptionMap.clear();
                        menuOptionMap.putAll(menuOptions);
                        nextToken.remove(MenuOption.class);
                        if (nextToken.isEmpty()) {
                            List<MenuOptionCategory> combinedMenuOptionCategory = mapMenuOptionCategoryWithMenuOption(menuOptionCategoryList, menuOptionMap, menuList, menuOptionDetailMap);
                            if (!combinedMenuOptionCategory.isEmpty()) {
                                e.onNext(combinedMenuOptionCategory);
                            }
                        }
                        completeToken.remove(MenuOption.class);
                        if (completeToken.isEmpty()) {
                            e.onComplete();
                        }
                    }).subscribeOn(Schedulers.newThread())
                    .subscribe();

            nextToken.add(Menu.class);
            completeToken.add(Menu.class);
            menuRepository.getMenuListOfStore(storeUuid)
                    .doOnNext(menus -> {
                        menuList.clear();
                        menuList.addAll(menus);
                        nextToken.remove(Menu.class);
                        if (nextToken.isEmpty()) {
                            List<MenuOptionCategory> combinedMenuOptionCategory = mapMenuOptionCategoryWithMenuOption(menuOptionCategoryList, menuOptionMap, menuList, menuOptionDetailMap);
                            if (!combinedMenuOptionCategory.isEmpty()) {
                                e.onNext(combinedMenuOptionCategory);
                            }
                        }
                    })
                    .doOnComplete(() -> {
                        completeToken.remove(Menu.class);
                        if (completeToken.isEmpty()) {
                            e.onComplete();
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .subscribe();

            nextToken.add(MenuOptionDetail.class);
            completeToken.add(MenuOptionDetail.class);
            menuRepository.getMenuOptionDetailListByStoreUuid(storeUuid)
                    .subscribeOn(Schedulers.newThread())
                    .flatMapSingle(Observable::toList)
                    .toMap(menuOptionDetails -> menuOptionDetails.get(0).menuOptionCategoryUuid)
                    .doOnSuccess(menuOptionDetails -> {
                        menuOptionDetailMap.clear();
                        menuOptionDetailMap.putAll(menuOptionDetails);
                        nextToken.remove(MenuOptionDetail.class);
                        if (nextToken.isEmpty()) {
                            List<MenuOptionCategory> combinedMenuOptionCategory = mapMenuOptionCategoryWithMenuOption(menuOptionCategoryList, menuOptionMap, menuList, menuOptionDetailMap);
                            if (!combinedMenuOptionCategory.isEmpty()) {
                                e.onNext(combinedMenuOptionCategory);
                            }
                        }
                        completeToken.remove(MenuOptionDetail.class);
                        if (completeToken.isEmpty()) {
                            e.onComplete();
                        }
                    }).subscribeOn(Schedulers.newThread())
                    .subscribe();

        }).subscribeOn(Schedulers.io());
    }

    private List<MenuOptionCategory> mapMenuOptionCategoryWithMenuOption(@NonNull final List<MenuOptionCategory> menuOptionCategoryList,
                                                                         @NonNull final Map<String, List<MenuOption>> menuOptionMap,
                                                                         @NonNull final List<Menu> menuList,
                                                                         @NonNull final Map<String, List<MenuOptionDetail>> menuOptionDetailMap) {
        final List<MenuOptionCategory> resultMenuOptionCategoryList = new ArrayList<>(menuOptionCategoryList);
        final Map<String, Menu> menuMap = new HashMap<>();
        for (Menu menu : menuList) {
            menuMap.put(menu.uuid, menu);
        }

        for (MenuOptionCategory menuOptionCategory : resultMenuOptionCategoryList) {
            if (menuOptionMap.containsKey(menuOptionCategory.uuid)) {
                menuOptionCategory.menuOptionList = new ArrayList<>();
                menuOptionCategory.menuOptionList.addAll(menuOptionMap.get(menuOptionCategory.uuid));
                Collections.sort(menuOptionCategory.menuOptionList, (o1, o2) -> o1.seqNum - o2.seqNum);
            }

            if (menuOptionDetailMap.containsKey(menuOptionCategory.uuid)) {
                List<Menu> includedMenuList = new ArrayList<>();
                for (MenuOptionDetail menuOptionDetail : menuOptionDetailMap.get(menuOptionCategory.uuid)) {
                    if (!menuMap.containsKey(menuOptionDetail.menuUuid)) {
                        continue;
                    }

                    includedMenuList.add(menuMap.get(menuOptionDetail.menuUuid));
                }
                menuOptionCategory.menuList = new ArrayList<>();
                menuOptionCategory.menuList.addAll(includedMenuList);
            }
        }

        return resultMenuOptionCategoryList;
    }

    public Observable<List<MenuOption>> getMenuOptionListByStoreUuid(String storeUuid) {
        return menuRepository.getMenuOptionListByStoreUuid(storeUuid)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuOptionCategory>> getMenuOptionCategoryListByStoreUuid(String storeUuid) {
        return menuRepository.getMenuOptionCategoryListByStoreUuid(storeUuid)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuOptionCategory>> getMenuOptionCategoryListByMenuUuid(String menuUuid) {
        return menuRepository.getMenuOptionDetailListByMenuUuid(menuUuid)
                .flatMapMaybe(menuOptionDetailList -> Observable.fromIterable(menuOptionDetailList)
                        .map(menuOptionDetail -> menuOptionDetail.menuOptionCategoryUuid)
                        .flatMap(menuRepository::getMenuOptionCategory)
                        .toList().toMaybe()
                ).subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> createMenuOptionDetailListAsMenuOptionCategory(final String storeUuid,
                                                                            final String menuOptionCategoryUuid,
                                                                            final List<Menu> menuList) {

        return Observable.fromIterable(menuList)
                .map(menu -> menu.uuid)
                .map(menuUuid -> new MenuOptionDetail.Builder()
                        .setStoreUuid(storeUuid)
                        .setMenuOptionCategoryUuid(menuOptionCategoryUuid)
                        .setMenuUuid(menuUuid)
                        .build())
                .toList().toMaybe()
                .flatMap(menuRepository::createMenuOptionDetailList)
                .flatMap(menuOptionDetailList -> Observable.fromIterable(menuOptionDetailList)
                        .map(menuOptionDetail -> menuOptionDetail.menuUuid)
                        .flatMapMaybe(menuRepository::getMenuFromDisk)
                        .toList().toMaybe()
                ).subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> createMenuOptionDetailListAsMenu(final String storeUuid,
                                                              final String menuUuid,
                                                              final Set<String> menuOptionCategoryUuidList) {

        return Observable.fromIterable(menuOptionCategoryUuidList)
                .map(menuCategoryUuid -> new MenuOptionDetail.Builder()
                        .setStoreUuid(storeUuid)
                        .setMenuOptionCategoryUuid(menuCategoryUuid)
                        .setMenuUuid(menuUuid)
                        .build())
                .toList().toMaybe()
                .flatMap(menuRepository::createMenuOptionDetailList)
                .flatMap(menuOptionDetailList -> Observable.fromIterable(menuOptionDetailList)
                        .map(menuOptionDetail -> menuOptionDetail.menuUuid)
                        .flatMapMaybe(menuRepository::getMenuFromDisk)
                        .toList().toMaybe()
                ).subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOptionCategory> createMenuOptionCategory(String storeUuid, String name) {
        MenuOptionCategory menuOptionCategory = MenuOptionCategory.create(name, storeUuid);
        return menuRepository.createMenuOptionCategory(menuOptionCategory)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOptionCategory> createMenuOptionCategory(String storeUuid, String name,
                                                              List<MenuOption> menuOptionList) {
        return createMenuOptionCategory(storeUuid, name)
                .flatMap(menuOptionCategory -> createMenuOptionList(menuOptionCategory, menuOptionList)
                        .doOnSuccess(createdMenuOptionList -> menuOptionCategory.menuOptionList = createdMenuOptionList)
                        .map(x -> menuOptionCategory))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuOption>> createMenuOptionList(MenuOptionCategory menuOptionCategory, List<MenuOption> menuOptionList) {
        for (MenuOption menuOption : menuOptionList) {
            menuOption.menuOptionCategoryUuid = menuOptionCategory.uuid;
        }
        return menuRepository.createMenuOptionList(menuOptionList)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuOptionCategory>> deleteMenuOptionCategories(List<MenuOptionCategory> menuOptionCategoryList) {
        return menuRepository.deleteMenuOptionCategories(menuOptionCategoryList)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuOptionCategory>> updateMenuOptionCategories(List<MenuOptionCategory> menuOptionCategoryList) {
        return menuRepository.updateMenuOptionCategories(menuOptionCategoryList)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuOptionCategory>> updateMenuOptionCategoriesOfMenu(String storeUuid, String menuUuid, Set<String> menuOptionCategoryUuidList) {
        return Observable.fromIterable(menuOptionCategoryUuidList)
                .map(menuOptionCategoryUuid -> new MenuOptionDetail("", menuUuid, menuOptionCategoryUuid, storeUuid, 0))
                .toList().toMaybe()
                .flatMap(menuOptionCategoryList -> menuRepository.updateMenuOptionCategoriesOfMenu(menuUuid, menuOptionCategoryList))
                .flatMap(menuOptionDetailList -> Observable.fromIterable(menuOptionDetailList)
                        .map(menuOptionDetail -> menuOptionDetail.menuOptionCategoryUuid)
                        .flatMap(menuRepository::getMenuOptionCategory)
                        .toList().toMaybe()
                ).subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuOption>> deleteMenuOptions(List<MenuOption> menuOptionList) {
        return menuRepository.deleteMenuOptions(menuOptionList)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> deleteMenuOptionDetails(String menuOptionCategoryUuid, List<Menu> menuList) {
        return Observable.fromIterable(menuList)
                .map(menu -> menu.uuid)
                .flatMapMaybe(menuUuid -> menuRepository.getMenuOptionDetailFromDisk(menuOptionCategoryUuid, menuUuid))
                .flatMapMaybe(menuRepository::deleteMenuOptionDetail)
                .map(menuOptionDetail -> menuOptionDetail.menuUuid)
                .flatMapMaybe(menuRepository::getMenuFromDisk)
                .toList().toMaybe()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuOption>> updateMenuOptions(List<MenuOption> menuOptionList) {
        return menuRepository.updateMenuOptions(menuOptionList)
                .subscribeOn(Schedulers.io());
    }
}
