/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.repository.MenuRepository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuManager {
    INSTANCE;

    private MenuRepository menuRepository;

    MenuManager() {
        menuRepository = MenuRepository.INSTANCE;
    }

    public Observable<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        return menuRepository.getMenuOptionList(menuOptionUuidList)
                .subscribeOn(Schedulers.io());
    }
}
