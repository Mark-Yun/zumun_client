/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.core.entity.MenuOptionCategory;
import com.mark.zumo.client.core.repository.CategoryRepository;
import com.mark.zumo.client.core.repository.MenuDetailRepository;
import com.mark.zumo.client.core.repository.MenuRepository;
import com.mark.zumo.client.core.repository.SessionRepository;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.store.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private final static String TAG = "StoreMenuManager";

    private final Maybe<MenuRepository> menuRepositoryMaybe;
    private final Maybe<CategoryRepository> categoryRepositoryMaybe;
    private final Maybe<MenuDetailRepository> menuDetailRepositoryMaybe;

    private final SessionRepository sessionRepository;

    StoreMenuManager() {
        sessionRepository = SessionRepository.INSTANCE;

        menuRepositoryMaybe = sessionRepository.getStoreSession()
                .map(MenuRepository::getInstance);
        categoryRepositoryMaybe = sessionRepository.getStoreSession()
                .map(CategoryRepository::getInstance);
        menuDetailRepositoryMaybe = sessionRepository.getStoreSession()
                .map(MenuDetailRepository::getInstance);
    }

    public Observable<List<Menu>> getMenuList(String storeUuid) {
        return menuRepositoryMaybe.flatMapObservable(menuRepository -> menuRepository.getMenuListOfStore(storeUuid))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuCategory>> getMenuCategoryList(String storeUuid) {
        return sessionRepository.getStoreSession()
                .map(CategoryRepository::getInstance)
                .flatMapObservable(categoryRepository -> categoryRepository.getMenuCategoryList(storeUuid))
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
            List<MenuCategory> menuCategoryList = new ArrayList<>();
            List<Menu> menuList = new ArrayList<>();
            Map<String, List<MenuDetail>> menuDetailMap = new HashMap<>();

            Set<Class> nextToken = new CopyOnWriteArraySet<>();
            Set<Class> completeToken = new CopyOnWriteArraySet<>();

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
            menuDetailRepositoryMaybe.flatMapSingle(menuDetailRepository ->
                    menuDetailRepository.getMenuDetailListOfStore(storeUuid)
                            .subscribeOn(Schedulers.newThread())
                            .flatMapSingle(Observable::toList)
                            .toMap(menuDetail -> menuDetail.get(0).menuCategoryUuid))
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
        return menuDetailRepositoryMaybe.flatMap(menuDetailRepository -> menuDetailRepository.getMenuDetailListFromDisk(storeUuid)
                .flatMapObservable(Observable::fromIterable)
                .map(menuDetail -> menuDetail.menuUuid)
                .distinct()
                .toList()
                .flatMapMaybe(menuUuidList ->
                        menuRepositoryMaybe.flatMap(menuRepository ->
                                menuRepository.getMenuItemsOfStoreFromDisk(storeUuid)
                                        .flatMapObservable(Observable::fromIterable)
                                        .filter(menu -> !menuUuidList.contains(menu.uuid))
                                        .toList()
                                        .toMaybe())
                )).subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> getMenuFromDisk(String menuUuid) {
        return menuRepositoryMaybe.flatMap(menuRepository -> menuRepository.getMenuFromDisk(menuUuid))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> getMenuFromApi(String menuUuid) {
        return menuRepositoryMaybe.flatMap(menuRepository -> menuRepository.getMenuFromApi(menuUuid))
                .subscribeOn(Schedulers.io());
    }

    public Observable<Menu> getMenu(String menuUuid) {
        return menuRepositoryMaybe.flatMapObservable(menuRepository -> menuRepository.getMenu(menuUuid))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuDetail>> getMenuDetailListFromDisk(String storeUuid, String menuUuid) {
        return menuDetailRepositoryMaybe.flatMap(menuDetailRepository ->
                menuDetailRepository.getMenuDetailListFromDiskByMenuUuid(storeUuid, menuUuid)
                        .doOnSuccess(menuDetailList -> Log.d(TAG, "getMenuDetailListFromDisk: " + menuDetailList))
        ).subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuDetail>> getMenuDetailListByCategoryUuid(String categoryUuid) {
        return menuDetailRepositoryMaybe.flatMapObservable(menuDetailRepository -> menuDetailRepository.getMenuDetailListByCategoryUuid(categoryUuid))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<MenuCategory> getMenuCategoryFromDisk(String categoryUuid) {
        return categoryRepositoryMaybe.flatMap(categoryRepository -> categoryRepository.getMenuCategoryFromDisk(categoryUuid)
                .doOnSuccess(menuCategory -> Log.d(TAG, "getMenuCategoryFromDisk: " + menuCategory))
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> updateMenuName(final String menuUuid, final String menuName) {
        return menuRepositoryMaybe.flatMap(menuRepository -> menuRepository.getMenuFromDisk(menuUuid)
                .map(menu ->
                        new Menu.Builder(menu)
                                .setName(menuName)
                                .build())
                .flatMap(menuRepository::updateMenu)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> updateMenuPrice(final String menuUuid, final int price) {
        return menuRepositoryMaybe.flatMap(menuRepository ->
                menuRepository.getMenuFromDisk(menuUuid)
                        .map(menu ->
                                new Menu.Builder(menu)
                                        .setPrice(price)
                                        .build())
                        .flatMap(menuRepository::updateMenu)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> updateMenuImageUrl(final String menuUuid, final String imageUrl) {
        return menuRepositoryMaybe.flatMap(menuRepository -> menuRepository.getMenuFromDisk(menuUuid)
                .map(menu ->
                        new Menu.Builder(menu)
                                .setImageUrl(imageUrl)
                                .build())
                .flatMap(menuRepository::updateMenu)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<MenuCategory> createMenuCategory(final String name, final String storeUuid, final int seqNum) {
        return categoryRepositoryMaybe.flatMap(categoryRepository ->
                categoryRepository.createMenuCategory(new MenuCategory("", name, storeUuid, seqNum))
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> createMenu(final Menu menu) {
        return menuRepositoryMaybe.flatMap(menuRepository ->
                menuRepository.createMenu(menu)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<MenuCategory> createMenuOptionCategory(final String name, final String storeUuid, final int seqNum) {
        return categoryRepositoryMaybe.flatMap(categoryRepository ->
                categoryRepository.createMenuCategory(new MenuCategory("", name, storeUuid, seqNum))
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> createMenuDetailListAsMenuList(final MenuCategory menuCategory,
                                                            final List<Menu> menuList) {
        return menuDetailRepositoryMaybe.flatMap(menuDetailRepository ->
                Observable.fromIterable(menuList)
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
                        )
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> createMenuDetailListAsMenuList(final String storeUuid,
                                                            final String menuUuid, final Set<String> menuCategoryUuidList) {
        return menuDetailRepositoryMaybe.flatMap(menuDetailRepository ->
                Observable.fromIterable(menuCategoryUuidList)
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
                        )
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<MenuOptionCategory> createMenuOptionCategory(String storeUuid, String name) {
        MenuOptionCategory menuOptionCategory = MenuOptionCategory.create(name, storeUuid);
        return menuRepositoryMaybe.flatMap(menuRepository -> menuRepository.createMenuOptionCategory(menuOptionCategory))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> removeMenuDetailListAsMenuList(final MenuCategory menuCategory,
                                                            final List<Menu> menuList) {

        return menuDetailRepositoryMaybe.flatMap(menuDetailRepository ->
                Observable.fromIterable(menuList)
                        .map(menu -> menu.uuid)
                        .flatMapMaybe(menuUuid -> menuDetailRepository.getMenuDetailByCategoryUuidAndMenuUuidFromDisk(menuCategory.uuid, menuUuid))
                        .toList().toMaybe()
                        .flatMap(menuDetailList -> menuDetailRepository.removeMenuDetailList(menuDetailList)
                                .flatMapObservable(Observable::fromIterable)
                                .map(menuDetail -> menuDetail.menuUuid)
                                .flatMapMaybe(this::getMenuFromApi)
                                .toList().toMaybe())
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<List<Menu>> updateMenuDetailSequenceAsMenuList(final MenuCategory menuCategory,
                                                                final List<Menu> menuList) {

        return menuDetailRepositoryMaybe.flatMap(menuDetailRepository ->
                Observable.fromIterable(menuList)
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
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuCategory>> updateMenuCategoryList(final List<MenuCategory> menuCategoryList) {
        return categoryRepositoryMaybe.flatMap(categoryRepository ->
                categoryRepository.updateMenuCategory(menuCategoryList)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<MenuCategory> updateMenuCategoryName(final MenuCategory menuCategory, final String name) {
        MenuCategory newCategory = new MenuCategory(menuCategory.uuid, name, menuCategory.storeUuid, menuCategory.seqNum);
        return categoryRepositoryMaybe.flatMap(categoryRepository ->
                categoryRepository.updateMenuCategory(newCategory)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuCategory>> deleteCategories(final List<MenuCategory> menuCategoryList) {
        return Observable.fromIterable(menuCategoryList)
                .map(menuCategory -> menuCategory.uuid)
                .flatMapMaybe(categoryUuid -> categoryRepositoryMaybe.flatMap(categoryRepository -> categoryRepository.deleteCategory(categoryUuid)))
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
                .flatMap(menuDetailList -> menuDetailRepositoryMaybe.flatMap(
                        menuDetailRepository -> menuDetailRepository.updateCategoriesOfMenu(menuUuid, menuDetailList)
                                .flatMapObservable(Observable::fromIterable)
                                .map(menuDetail -> menuDetail.menuCategoryUuid)
                                .flatMapMaybe(categoryUuid -> categoryRepositoryMaybe.flatMap(categoryRepository -> categoryRepository.getMenuCategoryFromDisk(categoryUuid)))
                                .toList().toMaybe())
                ).subscribeOn(Schedulers.io());
    }

    public Maybe<List<MenuDetail>> updateMenusOfCategory(final String storeUuid,
                                                         final String categoryUuid,
                                                         final List<String> menuUuidList) {
        return Observable.fromIterable(menuUuidList)
                .map(menuUuid -> new MenuDetail.Builder()
                        .setMenuUuid(menuUuid)
                        .setStoreUuid(storeUuid)
                        .setMenuCategoryUuid(categoryUuid)
                        .setMenuSeqNum(menuUuidList.indexOf(menuUuid))
                        .build()).toList().toMaybe()
                .flatMap(menuDetailList -> menuDetailRepositoryMaybe.flatMap(
                        menuDetailRepository -> menuDetailRepository.updateMenusOfCategory(categoryUuid, menuDetailList))
                ).subscribeOn(Schedulers.io());
    }
}
