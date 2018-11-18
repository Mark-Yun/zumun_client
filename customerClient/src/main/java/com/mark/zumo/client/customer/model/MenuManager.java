/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.core.p2p.packet.CombinedResult;
import com.mark.zumo.client.core.repository.CategoryRepository;
import com.mark.zumo.client.core.repository.MenuDetailRepository;
import com.mark.zumo.client.core.repository.MenuRepository;
import com.mark.zumo.client.core.repository.SessionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuManager {
    INSTANCE;

    public static final String TAG = "MenuManager";
    private final SessionRepository sessionRepository;

    private P2pClient p2pClient;

    private Maybe<MenuRepository> menuRepositoryMaybe;
    private Maybe<CategoryRepository> categoryRepositoryMaybe;
    private Maybe<MenuDetailRepository> menuDetailRepositoryMaybe;

    MenuManager() {
        sessionRepository = SessionRepository.INSTANCE;

        menuRepositoryMaybe = sessionRepository.getCustomerSession()
                .map(MenuRepository::getInstance);
        categoryRepositoryMaybe = sessionRepository.getCustomerSession()
                .map(CategoryRepository::getInstance);
        menuDetailRepositoryMaybe = sessionRepository.getCustomerSession()
                .map(MenuDetailRepository::getInstance);

    }

    public Maybe<Menu> getMenuFromDisk(String uuid) {
        return menuRepositoryMaybe.flatMap(menuRepository -> menuRepository.getMenuFromDisk(uuid))
                .subscribeOn(Schedulers.io());
    }

    public void clearClient() {
        if (p2pClient == null) {
            return;
        }

        p2pClient.stopDiscovery();
        p2pClient = null;
    }

    public Observable<GroupedObservable<String, MenuOption>> getMenuOptionList(String menuUuid) {
        return menuRepositoryMaybe.flatMapObservable(
                menuRepository -> menuRepository.getMenuOptionByMenuUuid(menuUuid)
                        .distinctUntilChanged()
                        .flatMap(Observable::fromIterable)
                        .groupBy(menuOption -> menuOption.name)
        ).subscribeOn(Schedulers.computation());
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

            menuRepositoryMaybe.flatMapObservable(menuRepository -> menuRepository.getMenuListOfStore(storeUuid))
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
            if (!menuDetailMap.containsKey(menuCategory.uuid)) {
                continue;
            }

            List<MenuDetail> menuDetailList = menuDetailMap.get(menuCategory.uuid);
            if (menuDetailList.size() > 1) {
                Collections.sort(menuDetailList, (o1, o2) -> o1.menuSeqNum - o2.menuSeqNum);
            }

            List<Menu> categoryMenuList = new ArrayList<>();
            for (MenuDetail menuDetail : menuDetailList) {
                categoryMenuList.add(menuMap.get(menuDetail.menuUuid));
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

        return resultMenuCategoryList;
    }

    public Maybe<MenuOption> getMenuOptionFromDisk(String menuOptionUuid) {
        return menuRepositoryMaybe.flatMap(menuRepository -> menuRepository.getMenuOptionFromDisk(menuOptionUuid))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        return menuRepositoryMaybe.flatMapObservable(menuRepository -> menuRepository.getMenuOptionList(menuOptionUuidList))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuCategory>> getMenuCategoryList(String storeUuid) {
        return sessionRepository.getCustomerSession()
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
}
