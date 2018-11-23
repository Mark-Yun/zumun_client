/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.util.Log;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOptionDetail;
import com.mark.zumo.client.core.p2p.packet.CombinedResult;
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

    private final Maybe<MenuRepository> menuRepositoryMaybe;
    private final Maybe<CategoryRepository> categoryRepositoryMaybe;
    private final Maybe<MenuDetailRepository> menuDetailRepositoryMaybe;

    private final SessionRepository sessionRepository;

    MenuManager() {
        sessionRepository = SessionRepository.INSTANCE;

        menuRepositoryMaybe = sessionRepository.getStoreSession()
                .map(MenuRepository::getInstance);
        categoryRepositoryMaybe = sessionRepository.getStoreSession()
                .map(CategoryRepository::getInstance);
        menuDetailRepositoryMaybe = sessionRepository.getStoreSession()
                .map(MenuDetailRepository::getInstance);
    }

    public Observable<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        return menuRepositoryMaybe.flatMapObservable(menuRepository -> menuRepository.getMenuOptionList(menuOptionUuidList))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuOptionDetail>> getMenuOptionDetailByMenuOptionUuid(String menuOptionUuid) {
        return menuRepositoryMaybe.flatMapObservable(menuRepository -> menuRepository.getMenuOptionDetailListByMenuOptionUuid(menuOptionUuid))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Menu>> getMenuList(String storeUuid) {
        return menuRepositoryMaybe.flatMapObservable(menuRepository -> menuRepository.getMenuListOfStore(storeUuid))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuOption>> getMenuOptionListByStoreUuid(String storeUuid) {
        return menuRepositoryMaybe.flatMapObservable(
                menuRepository -> menuRepository.getMenuOptionListByStoreUuid(storeUuid)
        ).subscribeOn(Schedulers.io());
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
        return Observable.create(e -> {
            List<MenuCategory> menuCategoryList = new ArrayList<>();
            List<Menu> menuList = new ArrayList<>();
            Map<String, List<MenuDetail>> menuDetailMap = new HashMap<>();

            getMenuCategoryList(storeUuid)
                    .subscribeOn(Schedulers.newThread())
                    .doOnNext(menuCategories -> {
                        menuCategoryList.clear();
                        menuCategoryList.addAll(menuCategories);
                        if (!menuCategoryList.isEmpty() && !menuList.isEmpty() && !menuDetailMap.isEmpty()) {
                            List<MenuCategory> mappedCategoryList = mapCategoryWithMenu(menuCategoryList, menuList, menuDetailMap);
                            e.onNext(mappedCategoryList);
                        }
                    })
                    .subscribe();

            menuDetailRepositoryMaybe.flatMapSingle(menuDetailRepository ->
                    menuDetailRepository.getMenuDetailListOfStore(storeUuid)
                            .subscribeOn(Schedulers.newThread())
                            .flatMapSingle(Observable::toList)
                            .toMap(menuDetail -> menuDetail.get(0).menuCategoryUuid))
                    .doOnSuccess(createdMenuDetailMap -> {
                        menuDetailMap.clear();
                        menuDetailMap.putAll(createdMenuDetailMap);
                        if (!menuCategoryList.isEmpty() && !menuList.isEmpty() && !menuDetailMap.isEmpty()) {
                            List<MenuCategory> mappedCategoryList = mapCategoryWithMenu(menuCategoryList, menuList, menuDetailMap);
                            e.onNext(mappedCategoryList);
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .subscribe();

            getMenuList(storeUuid)
                    .subscribeOn(Schedulers.newThread())
                    .doOnNext(menus -> {
                        menuList.clear();
                        menuList.addAll(menus);
                        if (!menuCategoryList.isEmpty() && !menuList.isEmpty() && !menuDetailMap.isEmpty()) {
                            List<MenuCategory> mappedCategoryList = mapCategoryWithMenu(menuCategoryList, menuList, menuDetailMap);
                            e.onNext(mappedCategoryList);
                        }
                    })
                    .subscribe();
        });
    }

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

    public Maybe<Map<String, List<Menu>>> getMenuListByCategory(String storeUuid) {
        return menuRepositoryMaybe.flatMapObservable(menuRepository -> menuRepository.getMenuListOfStore(storeUuid))
                .filter(menuList -> menuList.size() > 0)
                .flatMap(x -> menuDetailRepositoryMaybe.flatMapObservable(a -> a.getMenuDetailListOfStore(storeUuid)))
                .flatMapSingle(groupedObservable ->
                        Single.zip(
                                Single.just(groupedObservable.getKey()),
                                groupedObservable.sorted((d1, d2) -> d1.menuSeqNum - d2.menuSeqNum)
                                        .map(menuDetail -> menuDetail.menuUuid)
                                        .map(uuid -> menuRepositoryMaybe.blockingGet().getMenuFromDisk(uuid).blockingGet())
                                        .toList(),
                                CombinedResult::new
                        )
                ).toMap(combinedResult -> combinedResult.t, combinedResult -> combinedResult.r)
                .toMaybe()
                .subscribeOn(Schedulers.computation());
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
                .doOnSuccess(menu -> menu.name = menuName)
                .flatMap(menuRepository::updateMenu)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> updateMenuPrice(final String menuUuid, final int price) {
        return menuRepositoryMaybe.flatMap(menuRepository ->
                menuRepository.getMenuFromDisk(menuUuid)
                        .doOnSuccess(menu -> menu.price = price)
                        .flatMap(menuRepository::updateMenu)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<Menu> updateMenuImageUrl(final String menuUuid, final String imageUrl) {
        return menuRepositoryMaybe.flatMap(menuRepository -> menuRepository.getMenuFromDisk(menuUuid)
                .doOnSuccess(menu -> menu.imageUrl = imageUrl)
                .flatMap(menuRepository::updateMenu)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<MenuCategory> createMenuCategory(final String name, final String storeUuid, final int seqNum) {
        return categoryRepositoryMaybe.flatMap(categoryRepository ->
                categoryRepository.createMenuCategory(new MenuCategory(null, name, storeUuid, seqNum))
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
