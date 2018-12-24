/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.store.model.MenuOptionManager;
import com.mark.zumo.client.store.model.S3TransferManager;
import com.mark.zumo.client.store.model.StoreMenuManager;
import com.mark.zumo.client.store.model.StoreSessionManager;

import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 26.
 */
public class MenuSettingViewModel extends AndroidViewModel {

    private final StoreSessionManager storeSessionManager;
    private final StoreMenuManager storeMenuManager;
    private final S3TransferManager s3TransferManager;
    private final MenuOptionManager menuOptionManager;

    private final CompositeDisposable disposables;

    private MutableLiveData<List<MenuCategory>> categoryListLiveData;

    public MenuSettingViewModel(@NonNull final Application application) {
        super(application);

        storeSessionManager = StoreSessionManager.INSTANCE;
        storeMenuManager = StoreMenuManager.INSTANCE;
        s3TransferManager = S3TransferManager.INSTANCE;
        menuOptionManager = MenuOptionManager.INSTANCE;

        disposables = new CompositeDisposable();
    }

    public LiveData<Menu> getMenuFromDisk(String menuUuid) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        storeMenuManager.getMenuFromDisk(menuUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<Menu> getMenu(String menuUuid) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        storeMenuManager.getMenu(menuUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<Menu> updateMenuName(String menuUuid, String menuName) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        storeMenuManager.updateMenuName(menuUuid, menuName)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<Menu> updateMenuPrice(String menuUuid, int menuPrice) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        storeMenuManager.updateMenuPrice(menuUuid, menuPrice)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<List<MenuCategory>> getMenuCategoryList() {
        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();
        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(storeMenuManager::getMenuCategoryList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<List<Menu>> getMenuList() {
        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();
        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(storeMenuManager::getMenuList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public MutableLiveData<List<Menu>> loadUnCategorizedMenu() {
        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();
        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(storeMenuManager::unCategorizedMenu)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();
        return liveData;
    }

    public MutableLiveData<List<MenuCategory>> updateMenuCategoriesOfMenu(final String menuUuid,
                                                                          final Set<String> categoryUuidSet) {

        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();

        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(storeUuid -> storeMenuManager.updateCategoriesOfMenu(storeUuid, menuUuid, categoryUuidSet))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    public void loadCombinedMenuCategoryList() {
        if (categoryListLiveData == null) {
            categoryListLiveData = new MutableLiveData<>();
        }

        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(storeMenuManager::getCombinedMenuCategoryList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(categoryListLiveData::setValue)
                .subscribe();
    }

    public LiveData<List<MenuCategory>> getMenuCategoryListByMenuUuid(final String menuUuid) {
        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();
        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(storeUuid -> storeMenuManager.getMenuDetailListFromDisk(storeUuid, menuUuid))
                .flatMapObservable(Observable::fromIterable)
                .map(menuDetail -> menuDetail.menuCategoryUuid)
                .flatMapMaybe(storeMenuManager::getMenuCategoryFromDisk)
                .toList().toMaybe()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }


    public LiveData<List<MenuCategory>> getCombinedMenuCategoryList() {
        loadCombinedMenuCategoryList();
        return categoryListLiveData;
    }

    public LiveData<MenuCategory> createMenuCategory(final String categoryName, int seqNum) {
        MutableLiveData<MenuCategory> liveData = new MutableLiveData<>();
        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(storeUuid -> storeMenuManager.createMenuCategory(categoryName, storeUuid, seqNum))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuCategory>> updateMenuCategorySeqNum(final List<MenuCategory> menuCategoryList) {
        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();
        storeMenuManager.updateMenuCategoryList(menuCategoryList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<MenuCategory> updateMenuCategoryName(final MenuCategory menuCategory, String newName) {
        MutableLiveData<MenuCategory> liveData = new MutableLiveData<>();
        storeMenuManager.updateMenuCategoryName(menuCategory, newName)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuCategory>> removeMenuCategory(final List<MenuCategory> menuCategoryList) {
        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();
        storeMenuManager.deleteCategories(menuCategoryList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<Menu> createMenu(final Activity activity, final Menu menu,
                                     final Set<String> menuCategoryUuidList,
                                     final Set<String> menuOptionCategoryUuidList) {

        MutableLiveData<Menu> liveData = new MutableLiveData<>();
        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .map(storeUuid -> new Menu.Builder(menu)
                        .setStoreUuid(storeUuid)
                        .build())
                .flatMap(preparedMenu ->
                        storeMenuManager.createMenu(preparedMenu)
                                .flatMap(createdMenu ->
                                        s3TransferManager.uploadMenuImage(activity, createdMenu.uuid, Uri.parse(menu.imageUrl))
                                                .flatMap(url -> storeMenuManager.updateMenuImageUrl(createdMenu.uuid, url))
                                                .flatMap(updatedMenu ->
                                                        storeMenuManager.createMenuDetailListAsMenuList(updatedMenu.storeUuid, updatedMenu.uuid, menuCategoryUuidList)
                                                                .flatMap(x -> menuOptionManager.createMenuOptionDetailListAsMenu(updatedMenu.storeUuid, updatedMenu.uuid, menuOptionCategoryUuidList))
                                                                .map(x -> updatedMenu))
                                )
                ).observeOn(AndroidSchedulers.mainThread()).doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();


        return liveData;
    }

    public LiveData<Menu> uploadAndUpdateMenuImage(Activity activity, String menuUuid, Uri uri) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        s3TransferManager.uploadMenuImage(activity, menuUuid, uri)
                .flatMap(url -> storeMenuManager.updateMenuImageUrl(menuUuid, url))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    public LiveData<List<Menu>> createMenuDetailListAsMenuList(final MenuCategory menuCategory,
                                                               final List<Menu> menuList) {
        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();
        storeMenuManager.createMenuDetailListAsMenuList(menuCategory, menuList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<Menu>> removeMenuDetailList(final MenuCategory menuCategory,
                                                     final List<Menu> menuList) {
        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();
        storeMenuManager.removeMenuDetailListAsMenuList(menuCategory, menuList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<Menu>> updateMenuDetailSequence(final MenuCategory menuCategory,
                                                         final List<Menu> menuList) {
        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();
        storeMenuManager.updateMenuDetailSequenceAsMenuList(menuCategory, menuList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();
        return liveData;
    }


    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
