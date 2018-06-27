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
import android.widget.Toast;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.store.model.MenuManager;
import com.mark.zumo.client.store.model.SessionManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 26.
 */
public class MenuSettingViewModel extends AndroidViewModel {

    private SessionManager sessionManager;
    private MenuManager menuManager;

    private CompositeDisposable compositeDisposable;

    public MenuSettingViewModel(@NonNull final Application application) {
        super(application);

        sessionManager = SessionManager.INSTANCE;
        menuManager = MenuManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<Menu>> menuList() {
        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();

        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(menuManager::getMenuList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add)
                .doOnNext(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<List<MenuCategory>> categoryList() {
        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();
        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(menuManager::getMenuCategoryList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add)
                .doOnNext(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public LiveData<MenuCategory> createCategory(String categoryName, int seqNum) {
        MutableLiveData<MenuCategory> liveData = new MutableLiveData<>();
        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMap(storeUuid -> menuManager.createMenuCategory(categoryName, storeUuid, seqNum))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(compositeDisposable::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public void updateCategorySeqNum(List<MenuCategory> menuCategoryList) {
        Toast.makeText(getApplication(), "update SeqNum", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
    }
}
