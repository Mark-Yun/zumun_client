/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.core.database.entity.MenuCategory;
import com.mark.zumo.client.core.database.entity.OrderDetail;
import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.customer.model.CartManager;
import com.mark.zumo.client.customer.model.CustomerMenuManager;
import com.mark.zumo.client.customer.model.CustomerStoreManager;
import com.mark.zumo.client.customer.model.entity.Cart;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 10.
 */
public class MenuViewModel extends AndroidViewModel {

    private final CustomerMenuManager customerMenuManager;
    private final CartManager cartManager;
    private final CustomerStoreManager customerStoreManager;

    private final CompositeDisposable disposables;

    private String currentStoreUuid;

    public MenuViewModel(@NonNull final Application application) {
        super(application);

        customerMenuManager = CustomerMenuManager.INSTANCE;
        cartManager = CartManager.INSTANCE;
        customerStoreManager = CustomerStoreManager.INSTANCE;

        disposables = new CompositeDisposable();
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        customerMenuManager.clearClient();
    }

    public LiveData<Cart> getCart(String storeUuid) {
        currentStoreUuid = storeUuid;

        MutableLiveData<Cart> cartLiveData = new MutableLiveData<>();

        cartManager.getCart(storeUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(cartLiveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return cartLiveData;
    }

    public MutableLiveData<List<MenuCategory>> loadCombinedMenuCategoryList(String storeUuid) {
        MutableLiveData<List<MenuCategory>> liveData = new MutableLiveData<>();

        customerMenuManager.getCombinedMenuCategoryList(storeUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    public void addMenuToCart(Menu menu) {
        cartManager.getCart(currentStoreUuid)
                .firstElement()
                .doOnSuccess(cart -> cart.addCartItem(OrderDetail.fromMenu(menu)))
                .doOnSubscribe(disposables::add)
                .subscribe();
    }

    public LiveData<Store> getStore(String storeUuid) {
        MutableLiveData<Store> storeLiveData = new MutableLiveData<>();

        customerStoreManager.getStore(storeUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(storeLiveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return storeLiveData;
    }
}
