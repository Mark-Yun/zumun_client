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
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOptionDetail;
import com.mark.zumo.client.store.model.MenuManager;
import com.mark.zumo.client.store.model.SessionManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 26.
 */
public class MenuOptionSettingViewModel extends AndroidViewModel {

    private SessionManager sessionManager;
    private MenuManager menuManager;

    private CompositeDisposable disposables;

    public MenuOptionSettingViewModel(@NonNull final Application application) {
        super(application);

        sessionManager = SessionManager.INSTANCE;
        menuManager = MenuManager.INSTANCE;

        disposables = new CompositeDisposable();
    }

    public LiveData<List<MenuOption>> getMenuOptionList() {
        MutableLiveData<List<MenuOption>> liveData = new MutableLiveData<>();

        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(menuManager::getMenuOptionListByStoreUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();

        return liveData;
    }

    public LiveData<List<Menu>> getMenuList() {
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

    public LiveData<List<MenuOptionDetail>> getMenuOptionDetailList() {
        MutableLiveData<List<MenuOptionDetail>> liveData = new MutableLiveData<>();

        sessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(menuManager::getMenuOptionDetailByMenuOptionUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnNext(liveData::setValue)
                .subscribe();

        return liveData;
    }
}
