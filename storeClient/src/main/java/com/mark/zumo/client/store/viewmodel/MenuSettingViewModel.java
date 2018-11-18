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
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.store.model.MenuManager;
import com.mark.zumo.client.store.model.S3TransferManager;
import com.mark.zumo.client.store.model.SessionManager;

import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 26.
 */
public class MenuSettingViewModel extends AndroidViewModel {

    private final SessionManager sessionManager;
    private final MenuManager menuManager;
    private final S3TransferManager s3TransferManager;

    private final CompositeDisposable disposables;

    private MutableLiveData<List<MenuCategory>> categoryListLiveData;

    public MenuSettingViewModel(@NonNull final Application application) {
        super(application);

        sessionManager = SessionManager.INSTANCE;
        menuManager = MenuManager.INSTANCE;
        s3TransferManager = S3TransferManager.INSTANCE;

        disposables = new CompositeDisposable();
    }

    public LiveData<Menu> menuFromDisk(String menuUuid) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        menuManager.getMenuFromDisk(menuUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<Menu> getMenu(String menuUuid) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        menuManager.getMenu(menuUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<Menu> updateMenuName(String menuUuid, String menuName) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        menuManager.updateMenuName(menuUuid, menuName)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<Menu> updateMenuPrice(String menuUuid, int menuPrice) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        menuManager.updateMenuPrice(menuUuid, menuPrice)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<List<MenuCategory>> categoryList() {
        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();
        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(menuManager::getMenuCategoryList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<List<Menu>> menuList() {
        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();
        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(menuManager::getMenuList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuDetail>> menuDetailList(String categoryUuid) {
        MutableLiveData<List<MenuDetail>> liveData = new MutableLiveData<>();
        menuManager.getMenuDetailListByCategoryUuid(categoryUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public MutableLiveData<List<Menu>> loadUnCategorizedMenu() {
        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();
        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(menuManager::unCategorizedMenu)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();
        return liveData;
    }

    public MutableLiveData<List<MenuCategory>> updateMenuCategoriesOfMenu(final String menuUuid,
                                                                          final Set<String> categoryUuidSet) {

        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();

        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(storeUuid -> menuManager.updateCategoriesOfMenu(storeUuid, menuUuid, categoryUuidSet))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    private MutableLiveData<List<MenuCategory>> getCategoryListLiveData() {
        if (categoryListLiveData == null) {
            categoryListLiveData = new MutableLiveData<>();
        }
        return categoryListLiveData;
    }

    public void loadMenuListByCategory() {
        if (categoryListLiveData == null) {
            categoryListLiveData = new MutableLiveData<>();
        }

        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(menuManager::getCombinedMenuCategoryList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(categoryListLiveData::setValue)
                .subscribe();
    }

    public LiveData<List<MenuCategory>> categoryListByMenuUuid(String menuUuid) {
        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();
        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(storeUuid -> menuManager.getMenuDetailListFromDisk(storeUuid, menuUuid))
                .flatMapObservable(Observable::fromIterable)
                .map(menuDetail -> menuDetail.menuCategoryUuid)
                .flatMapMaybe(menuManager::getMenuCategoryFromDisk)
                .toList().toMaybe()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuCategory>> getCombinedMenuCategoryList() {
        loadMenuListByCategory();
        return categoryListLiveData;
    }

    public LiveData<MenuCategory> createCategory(String categoryName, int seqNum) {
        MutableLiveData<MenuCategory> liveData = new MutableLiveData<>();
        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(storeUuid -> menuManager.createMenuCategory(categoryName, storeUuid, seqNum))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuCategory>> updateCategorySeqNum(List<MenuCategory> menuCategoryList) {
        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();
        menuManager.updateMenuCategoryList(menuCategoryList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<MenuCategory> updateCategoryName(MenuCategory menuCategory, String newName) {
        MutableLiveData<MenuCategory> liveData = new MutableLiveData<>();
        menuManager.updateMenuCategoryName(menuCategory, newName)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<MenuCategory> removeCategory(MenuCategory menuCategory) {
        MutableLiveData<MenuCategory> liveData = new MutableLiveData<>();
        menuManager.removeCategory(menuCategory)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<Menu> uploadMenuImage(Activity activity, String menuUuid, Uri uri) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        s3TransferManager.uploadMenuImage(activity, menuUuid, uri)
                .flatMap(url -> menuManager.updateMenuImageUrl(menuUuid, url))
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
