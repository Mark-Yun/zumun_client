/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOptionCategory;
import com.mark.zumo.client.store.model.MenuOptionManager;
import com.mark.zumo.client.store.model.StoreMenuManager;
import com.mark.zumo.client.store.model.StoreSessionManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 26.
 */
public class MenuOptionSettingViewModel extends AndroidViewModel {

    private final StoreSessionManager storeSessionManager;
    private final StoreMenuManager storeMenuManager;
    private final MenuOptionManager menuOptionManager;

    private CompositeDisposable disposables;

    public MenuOptionSettingViewModel(@NonNull final Application application) {
        super(application);

        storeSessionManager = StoreSessionManager.INSTANCE;
        storeMenuManager = StoreMenuManager.INSTANCE;
        menuOptionManager = MenuOptionManager.INSTANCE;

        disposables = new CompositeDisposable();
    }

    public LiveData<List<MenuOption>> getMenuOptionList() {
        MutableLiveData<List<MenuOption>> liveData = new MutableLiveData<>();

        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(menuOptionManager::getMenuOptionListByStoreUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<MenuOptionCategory> createMenuOptionCategory(String name) {

        MutableLiveData<MenuOptionCategory> liveData = new MutableLiveData<>();

        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(storeUuid -> menuOptionManager.createMenuOptionCategory(storeUuid, name))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<List<Menu>> createMenuOptionDetailList(final String menuOptionCategoryUuid,
                                                           final List<Menu> menuList) {

        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();

        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(storeUuid -> menuOptionManager.createMenuOptionDetailListAsMenu(storeUuid, menuOptionCategoryUuid, menuList))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<List<MenuOptionCategory>> removeMenuOptionCategory(List<MenuOptionCategory> menuOptionCategoryList) {
        MutableLiveData<List<MenuOptionCategory>> liveData = new MutableLiveData<>();
        menuOptionManager.deleteMenuOptionCategories(menuOptionCategoryList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuOptionCategory>> reorderMenuOptionCategory(List<MenuOptionCategory> menuOptionCategoryList) {
        MutableLiveData<List<MenuOptionCategory>> liveData = new MutableLiveData<>();
        menuOptionManager.updateMenuOptionCategories(menuOptionCategoryList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuOption>> removeMenuOption(List<MenuOption> menuOptionList) {
        MutableLiveData<List<MenuOption>> liveData = new MutableLiveData<>();
        menuOptionManager.deleteMenuOptions(menuOptionList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<List<Menu>> removeMenuOptionDetail(MenuOptionCategory menuOptionCategory, List<Menu> menuList) {
        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();
        menuOptionManager.deleteMenuOptionDetails(menuOptionCategory.uuid, menuList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuOption>> reorderMenuOption(List<MenuOption> menuOptionList) {
        MutableLiveData<List<MenuOption>> liveData = new MutableLiveData<>();
        menuOptionManager.updateMenuOptions(menuOptionList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }


    public LiveData<List<MenuOptionCategory>> getCombinedMenuOptionCategoryList() {
        MutableLiveData<List<MenuOptionCategory>> liveData = new MutableLiveData<>();

        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(menuOptionManager::getCombinedMenuOptionCategoryListByStoreUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<List<MenuCategory>> getCategoryList() {
        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();

        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(storeMenuManager::getCombinedMenuCategoryList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<MenuOptionCategory> createMenuOptionCategory(String menuOptionCategoryName,
                                                                 List<MenuOption> menuOptionList) {

        MutableLiveData<MenuOptionCategory> liveData = new MutableLiveData<>();

        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(storeUuid -> menuOptionManager.createMenuOptionCategory(storeUuid, menuOptionCategoryName, menuOptionList))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }
}
