/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.core.database.entity.MenuCategory;
import com.mark.zumo.client.core.database.entity.MenuDetail;
import com.mark.zumo.client.core.repository.CategoryRepository;
import com.mark.zumo.client.core.repository.MenuDetailRepository;
import com.mark.zumo.client.core.repository.MenuRepository;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.store.R;

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
 * Created by mark on 18. 4. 30.
 */

public enum StoreMenuManager {

    INSTANCE;

    private static final String TAG = "StoreMenuManager";

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final MenuDetailRepository menuDetailRepository;

    StoreMenuManager() {

        menuRepository = MenuRepository.INSTANCE;
        categoryRepository = CategoryRepository.INSTANCE;
        menuDetailRepository = MenuDetailRepository.INSTANCE;
    }

    public Observable<List<Menu>> getMenuList(String storeUuid) {
        return menuRepository.getMenuListOfStore(storeUuid)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuCategory>> getMenuCategoryList(String storeUuid) {
        return categoryRepository.getMenuCategoryList(storeUuid)
                .flatMapSingle(menuCategories ->
                        Observable.fromIterable(menuCategories)
                                .sorted((c1, c2) -> c1.seqNum - c2.seqNum)
                                .toList()
                )
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuCategory>> getCombinedMenuCategoryList(String storeUuid) {
        return Observable.create((ObservableOnSubscribe<List<MenuCategory>>) e -> {
            final List<MenuCategory> menuCategoryList = new CopyOnWriteArrayList<>();
            final List<Menu> menuList = new CopyOnWriteArrayList<>();
            final Map<String, List<MenuDetail>> menuDetailMap = new ConcurrentHashMap<>();

            final Set<Class> nextToken = new CopyOnWriteArraySet<>();
            final Set<Class> completeToken = new CopyOnWriteArraySet<>();

            nextToken.add(MenuCategory.class);
            completeToken.add(MenuCategory.class);
            getMenuCategoryList(storeUuid)
                    .subscribeOn(Schedulers.newThread())
                    .doOnNext(menuCategories -> {
                        menuCategoryList.clear();
                        menuCategoryList.addAll(menuCategories);
                        nextToken.remove(MenuCategory.class);
                        if (nextToken.isEmpty()) {
                            List<MenuCategory> mappedCategoryList = mapCategoryWithMenu(menuCategoryList, menuList, menuDetailMap);
                            e.onNext(mappedCategoryList);
                        }
                    })
                    .doOnComplete(() -> {
                        completeToken.remove(MenuCategory.class);
                        if (completeToken.isEmpty()) {
                            e.onComplete();
                        }
                    })
                    .subscribe();

            nextToken.add(MenuDetail.class);
            completeToken.add(MenuDetail.class);
            menuDetailRepository.getMenuDetailListOfStore(storeUuid)
                    .subscribeOn(Schedulers.newThread())
                    .flatMapSingle(Observable::toList)
                    .toMap(menuDetail -> menuDetail.get(0).menuCategoryUuid)
                    .doOnSuccess(createdMenuDetailMap -> {
                        menuDetailMap.clear();
                        menuDetailMap.putAll(createdMenuDetailMap);
                        nextToken.remove(MenuDetail.class);
                        if (nextToken.isEmpty()) {
                            List<MenuCategory> mappedCategoryList = mapCategoryWithMenu(menuCategoryList, menuList, menuDetailMap);
                            e.onNext(mappedCategoryList);
                        }

                        completeToken.remove(MenuCategory.class);
                        if (completeToken.isEmpty()) {
                            e.onComplete();
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .subscribe();

            nextToken.add(Menu.class);
            completeToken.add(Menu.class);
            getMenuList(storeUuid)
                    .subscribeOn(Schedulers.newThread())
                    .doOnNext(menus -> {
                        menuList.clear();
                        menuList.addAll(menus);
                        nextToken.remove(Menu.class);
                        if (nextToken.isEmpty()) {
                            List<MenuCategory> mappedCategoryList = mapCategoryWithMenu(menuCategoryList, menuList, menuDetailMap);
                            e.onNext(mappedCategoryList);
                        }
                    }).doOnComplete(() -> {
                        completeToken.remove(MenuCategory.class);
                        if (completeToken.isEmpty()) {
                            e.onComplete();
                        }
                    }
            ).subscribe();
        }).subscribeOn(Schedulers.io());
    }

    @WorkerThread
    private List<MenuCategory> mapCategoryWithMenu(final List<MenuCategory> categoryList,
                                                   final List<Menu> menuList,
                                                   final Map<String, List<MenuDetail>> menuDetailMap) {

        List<MenuCategory> resultMenuCategoryList = new ArrayList<>();

        Map<String, Menu> menuMap = new HashMap<>();
        for (Menu menu : menuList) {
            menuMap.put(menu.uuid, menu);
        }

        for (MenuCategory menuCategory : categoryList) {
            List<MenuDetail> menuDetailList = menuDetailMap.get(menuCategory.uuid);
            List<Menu> categoryMenuList = new ArrayList<>();
            if (menuDetailList != null && !menuDetailList.isEmpty()) {
                Collections.sort(menuDetailList, (o1, o2) -> o1.menuSeqNum - o2.menuSeqNum);
                for (MenuDetail menuDetail : menuDetailList) {
                    categoryMenuList.add(menuMap.get(menuDetail.menuUuid));
                }
            }

            MenuCategory combinedMenuCategory = new MenuCategory(
                    menuCategory.uuid,
                    menuCategory.name,
                    menuCategory.storeUuid,
                    menuCategory.seqNum,
                    categoryMenuList
            );

            resultMenuCategoryList.add(combinedMenuCategory);
        }

        resultMenuCategoryList.add(createNoneCategory(categoryList, menuList, menuDetailMap));
        return resultMenuCategoryList;
    }

    private MenuCategory createNoneCategory(final List<MenuCategory> categoryList,
                                            final List<Menu> menuList,
                                            final Map<String, List<MenuDetail>> menuDetailMap) {

        String unCategorizedMenusName = ContextHolder.getContext().getString(R.string.un_categorized_menu_category_title);
        MenuCategory noneCategory = new MenuCategory("", unCategorizedMenusName, "", Integer.MAX_VALUE);
        List<Menu> unCategorizedMenuList = new ArrayList<>(menuList);
        for (List<MenuDetail> menuDetailList : menuDetailMap.values()) {
            for (MenuDetail menuDetail : menuDetailList) {
                for (Menu menu : menuList) {
                    if (hasMenuInMenuList(unCategorizedMenuList, menu)
                            && hasMenuCategoryInMenuCategoryList(categoryList, menuDetail.menuCategoryUuid)
                            && menu.uuid.equals(menuDetail.menuUuid)) {
                        unCategorizedMenuList.remove(menu);
                    }
                }
            }
        }

        noneCategory.menuList = unCategorizedMenuList;
        return noneCategory;
    }

    private boolean hasMenuInMenuList(List<Menu> menuList, Menu menu) {
        for (Menu menu1 : menuList) {
            if (menu1.uuid.equals(menu.uuid)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasMenuCategoryInMenuCategoryList(final List<MenuCategory> menuCategoryList,
                                                      final String menuCategoryUuid) {
        for (MenuCategory menuCategory1 : menuCategoryList) {
            if (menuCategory1.uuid.equals(menuCategoryUuid)) {
                return true;
            }
        }

        return false;
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

    public Maybe<Menu> getMenuFromApi(String menuUuid) {
        return menuRepository.getMenuFromApi(menuUuid)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Menu> getMenu(String menuUuid) {
        return menuRepository.getMenu(menuUuid)
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
                .doOnSuccess(menuCategory -> Log.d(TAG, "getMenuCategoryFromDisk: " + menuCategory))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> updateMenuName(final String menuUuid, final String menuName) {
        return menuRepository.getMenuFromDisk(menuUuid)
                .map(menu ->
                        new Menu.Builder(menu)
                                .setName(menuName)
                                .build())
                .flatMap(menuRepository::updateMenu)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> updateMenuPrice(final String menuUuid, final int price) {
        return menuRepository.getMenuFromDisk(menuUuid)
                .map(menu ->
                        new Menu.Builder(menu)
                                .setPrice(price)
                                .build())
                .flatMap(menuRepository::updateMenu)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> updateMenuImageUrl(final String menuUuid, final String imageUrl) {
        return menuRepository.getMenuFromDisk(menuUuid)
                .map(menu ->
                        new Menu.Builder(menu)
                                .setImageUrl(imageUrl)
                                .build())
                .flatMap(menuRepository::updateMenu)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuCategory> createMenuCategory(final String name, final String storeUuid, final int seqNum) {
        return categoryRepository.createMenuCategory(new MenuCategory("", name, storeUuid, seqNum))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> createMenu(final Menu menu) {
        return menuRepository.createMenu(menu)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuCategory> createMenuOptionCategory(final String name, final String storeUuid, final int seqNum) {
        return categoryRepository.createMenuCategory(new MenuCategory("", name, storeUuid, seqNum))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> createMenuDetailListAsMenuList(final MenuCategory menuCategory,
                                                            final List<Menu> menuList) {
        return Observable.fromIterable(menuList)
                .map(menu -> new MenuDetail.Builder()
                        .setMenuUuid(menu.uuid)
                        .setMenuCategoryUuid(menuCategory.uuid)
                        .setStoreUuid(menu.storeUuid)
                        .build())
                .toList().toMaybe()
                .flatMap(menuDetailList -> menuDetailRepository.createMenuDetailList(menuDetailList)
                        .flatMapObservable(Observable::fromIterable)
                        .map(menuDetail -> menuDetail.menuUuid)
                        .flatMapMaybe(this::getMenuFromApi)
                        .toList().toMaybe()
                ).subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> createMenuDetailListAsMenuList(final String storeUuid,
                                                            final String menuUuid, final Set<String> menuCategoryUuidList) {
        return Observable.fromIterable(menuCategoryUuidList)
                .map(menuCategoryUuid -> new MenuDetail.Builder()
                        .setMenuUuid(menuUuid)
                        .setMenuCategoryUuid(menuCategoryUuid)
                        .setStoreUuid(storeUuid)
                        .build())
                .toList().toMaybe()
                .flatMap(menuDetailList -> menuDetailRepository.createMenuDetailList(menuDetailList)
                        .flatMapObservable(Observable::fromIterable)
                        .map(menuDetail -> menuDetail.menuUuid)
                        .flatMapMaybe(this::getMenuFromApi)
                        .toList().toMaybe()
                ).subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> removeMenuDetailListAsMenuList(final MenuCategory menuCategory,
                                                            final List<Menu> menuList) {

        return Observable.fromIterable(menuList)
                .map(menu -> menu.uuid)
                .flatMapMaybe(menuUuid -> menuDetailRepository.getMenuDetailByCategoryUuidAndMenuUuidFromDisk(menuCategory.uuid, menuUuid))
                .toList().toMaybe()
                .flatMap(menuDetailList -> menuDetailRepository.removeMenuDetailList(menuDetailList)
                        .flatMapObservable(Observable::fromIterable)
                        .map(menuDetail -> menuDetail.menuUuid)
                        .flatMapMaybe(this::getMenuFromApi)
                        .toList().toMaybe())
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> updateMenuDetailSequenceAsMenuList(final MenuCategory menuCategory,
                                                                final List<Menu> menuList) {

        return Observable.fromIterable(menuList)
                .concatMapMaybe(menu -> menuDetailRepository.getMenuDetailByCategoryUuidAndMenuUuidFromDisk(menuCategory.uuid, menu.uuid))
                .toList().toMaybe()
                .map(menuDetailList -> {
                    for (MenuDetail menuDetail : menuDetailList)
                        menuDetail.menuSeqNum = menuDetailList.indexOf(menuDetail);
                    return menuDetailList;
                })
                .flatMap(menuDetailList -> menuDetailRepository.updateMenuDetailSequence(menuCategory.uuid, menuDetailList)
                        .flatMapObservable(Observable::fromIterable)
                        .sorted((o1, o2) -> o1.menuSeqNum - o2.menuSeqNum)
                        .map(menuDetail -> menuDetail.menuUuid)
                        .concatMapMaybe(this::getMenuFromDisk)
                        .toList().toMaybe())
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

    public Maybe<List<MenuCategory>> deleteCategories(final List<MenuCategory> menuCategoryList) {
        return Observable.fromIterable(menuCategoryList)
                .map(menuCategory -> menuCategory.uuid)
                .flatMapMaybe(categoryRepository::deleteCategory)
                .toList().toMaybe()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuCategory>> updateCategoriesOfMenu(final String storeUuid,
                                                            final String menuUuid,
                                                            final Set<String> categoryUuidSet) {
        return Observable.fromIterable(categoryUuidSet)
                .map(categoryUuid -> new MenuDetail.Builder()
                        .setMenuCategoryUuid(categoryUuid)
                        .setMenuUuid(menuUuid)
                        .setStoreUuid(storeUuid)
                        .build()).toList().toMaybe()
                .flatMap(menuDetailList -> menuDetailRepository.updateCategoriesOfMenu(menuUuid, menuDetailList)
                        .flatMapObservable(Observable::fromIterable)
                        .map(menuDetail -> menuDetail.menuCategoryUuid)
                        .flatMapMaybe(categoryRepository::getMenuCategoryFromDisk)
                        .toList().toMaybe())
                .subscribeOn(Schedulers.io());
    }

}
