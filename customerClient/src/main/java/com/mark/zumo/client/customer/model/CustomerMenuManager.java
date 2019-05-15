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
import com.mark.zumo.client.core.entity.MenuOptionCategory;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.core.repository.CategoryRepository;
import com.mark.zumo.client.core.repository.CustomerUserRepository;
import com.mark.zumo.client.core.repository.MenuDetailRepository;
import com.mark.zumo.client.core.repository.MenuRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum CustomerMenuManager {
    INSTANCE;

    public static final String TAG = "CustomerMenuManager";
    private final CustomerUserRepository customerUserRepository;

    private P2pClient p2pClient;

    private MenuRepository menuRepository;
    private MenuDetailRepository menuDetailRepository;
    private CategoryRepository categoryRepository;

    CustomerMenuManager() {
        customerUserRepository = CustomerUserRepository.INSTANCE;

        menuRepository = MenuRepository.INSTANCE;
        menuDetailRepository = MenuDetailRepository.INSTANCE;
        categoryRepository = CategoryRepository.INSTANCE;

    }

    public Maybe<Menu> getMenuFromDisk(String uuid) {
        return menuRepository.getMenuFromDisk(uuid)
                .subscribeOn(Schedulers.io());
    }

    public void clearClient() {
        if (p2pClient == null) {
            return;
        }

        p2pClient.stopDiscovery();
        p2pClient = null;
    }

    public Observable<List<MenuCategory>> getCombinedMenuCategoryList(String storeUuid) {
        return Observable.create(e -> {
            final List<MenuCategory> menuCategoryList = new CopyOnWriteArrayList<>();
            final List<Menu> menuList = new CopyOnWriteArrayList<>();
            final Map<String, List<MenuDetail>> menuDetailMap = new ConcurrentHashMap<>();

            final Set<Class> loadingToken = new CopyOnWriteArraySet<>();
            final Set<Class> completeToken = new CopyOnWriteArraySet<>();

            loadingToken.add(MenuCategory.class);
            completeToken.add(MenuCategory.class);
            getMenuCategoryList(storeUuid)
                    .subscribeOn(Schedulers.newThread())
                    .doOnNext(menuCategories -> {
                        menuCategoryList.clear();
                        menuCategoryList.addAll(menuCategories);

                        loadingToken.remove(MenuCategory.class);
                        if (loadingToken.isEmpty()) {
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

            loadingToken.add(MenuDetail.class);
            completeToken.add(MenuDetail.class);
            menuDetailRepository.getMenuDetailListOfStore(storeUuid)
                    .subscribeOn(Schedulers.newThread())
                    .flatMapSingle(Observable::toList)
                    .toMap(menuDetail -> menuDetail.get(0).menuCategoryUuid)
                    .doOnSuccess(createdMenuDetailMap -> {
                        menuDetailMap.clear();
                        menuDetailMap.putAll(createdMenuDetailMap);
                        loadingToken.remove(MenuDetail.class);

                        if (loadingToken.isEmpty()) {
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

            loadingToken.add(Menu.class);
            completeToken.add(Menu.class);
            menuRepository.getMenuListOfStore(storeUuid)
                    .subscribeOn(Schedulers.newThread())
                    .doOnNext(menus -> {
                        menuList.clear();
                        menuList.addAll(menus);
                        loadingToken.remove(Menu.class);

                        if (loadingToken.isEmpty()) {
                            List<MenuCategory> mappedCategoryList = mapCategoryWithMenu(menuCategoryList, menuList, menuDetailMap);
                            e.onNext(mappedCategoryList);
                        }
                    })
                    .doOnComplete(() -> {
                        completeToken.remove(Menu.class);
                        if (completeToken.isEmpty()) {
                            e.onComplete();
                        }
                    })
                    .subscribe();
        });
    }

    private List<MenuCategory> mapCategoryWithMenu(final List<MenuCategory> categoryList,
                                                   final List<Menu> menuList,
                                                   final Map<String, List<MenuDetail>> menuDetailMap) {

        List<MenuCategory> resultMenuCategoryList = new CopyOnWriteArrayList<>();

        Map<String, Menu> menuMap = new ConcurrentHashMap<>();
        for (Menu menu : menuList) {
            menuMap.put(menu.uuid, menu);
        }

        for (MenuCategory menuCategory : categoryList) {
            if (!menuDetailMap.containsKey(menuCategory.uuid)) {
                continue;
            }

            final List<MenuDetail> menuDetailList = menuDetailMap.get(menuCategory.uuid);
            if (menuDetailList != null && menuDetailList.size() > 1) {
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
        return menuRepository.getMenuOptionFromDisk(menuOptionUuid)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        return menuRepository.getMenuOptionList(menuOptionUuidList)
                .subscribeOn(Schedulers.io());
    }

    private Observable<List<MenuCategory>> getMenuCategoryList(String storeUuid) {
        return categoryRepository.getMenuCategoryList(storeUuid)
                .flatMapSingle(menuCategories ->
                        Observable.fromIterable(menuCategories)
                                .sorted((c1, c2) -> c1.seqNum - c2.seqNum)
                                .toList()
                )
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io());
    }


    public Observable<List<MenuOptionCategory>> getCombinedMenuOptionCategoryListByMenuUuid(String menuUuid) {
        return menuRepository.getMenuOptionDetailListByMenuUuid(menuUuid)
                .flatMapMaybe(menuOptionDetailList ->
                        Observable.fromIterable(menuOptionDetailList)
                                .sorted((o1, o2) -> o1.seqNum - o2.seqNum)
                                .map(menuOptionDetail -> menuOptionDetail.menuOptionCategoryUuid)
                                .flatMapMaybe(this::getCombinedMenuOptionCategory)
                                .toList().toMaybe()
                ).subscribeOn(Schedulers.io()
                );
    }

    public Maybe<MenuOptionCategory> getCombinedMenuOptionCategory(String menuOptionCategoryUuid) {
        return Maybe.create((MaybeOnSubscribe<MenuOptionCategory>) e -> {
            final List<MenuOptionCategory> menuOptionCategoryList = new CopyOnWriteArrayList<>();
            final List<MenuOption> menuOptionList = new CopyOnWriteArrayList<>();

            final Deque<Object> loadingToken = new ConcurrentLinkedDeque<>();

            menuRepository.getMenuOptionCategory(menuOptionCategoryUuid)
                    .lastElement()
                    .doOnSuccess(menuOptionCategoryList::add)
                    .doOnSuccess(x -> loadingToken.pop())
                    .doOnSuccess(x -> {
                        if (loadingToken.isEmpty()) {
                            MenuOptionCategory menuOptionCategory = menuOptionCategoryList.get(0);
                            menuOptionCategory.menuOptionList = new ArrayList<>(menuOptionList);
                            e.onSuccess(menuOptionCategory);
                            e.onComplete();
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .doOnSubscribe(x -> loadingToken.add(new Object()))
                    .subscribe();

            menuRepository.getMenuOptionListByMenuOptionCategoryUuid(menuOptionCategoryUuid)
                    .lastElement()
                    .doOnSuccess(menuOptionList::addAll)
                    .doOnSuccess(x -> loadingToken.pop())
                    .doOnSuccess(x -> {
                        if (loadingToken.isEmpty()) {
                            MenuOptionCategory menuOptionCategory = menuOptionCategoryList.get(0);
                            menuOptionCategory.menuOptionList = new ArrayList<>(menuOptionList);
                            e.onSuccess(menuOptionCategory);
                            e.onComplete();
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .doOnSubscribe(x -> loadingToken.add(new Object()))
                    .subscribe();
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<OrderDetail> createOrderDetail(String menuUuid, List<String> menuOptionUuidList, int amount) {
        return getMenuFromDisk(menuUuid)
                .flatMap(menu ->
                        Maybe.just(menu)
                                .flatMap(x -> getMenuOptionListPrice(menuOptionUuidList))
                                .map(optionPrice -> optionPrice + menu.price)
                                .map(singlePrice -> singlePrice * amount)
                                .map(totalPrice -> OrderDetail.create(menu.storeUuid, menu.uuid, menu.name, menuOptionUuidList, amount, totalPrice))
                );
    }

    private Maybe<Integer> getMenuOptionListPrice(List<String> menuOptionUuidList) {
        if (menuOptionUuidList == null || menuOptionUuidList.isEmpty()) {
            return Maybe.just(0);
        }

        return Observable.fromIterable(menuOptionUuidList)
                .flatMapMaybe(menuRepository::getMenuOptionFromDisk)
                .map(menuOption -> menuOption.price)
                .reduce((i, i2) -> i + i2);
    }
}
