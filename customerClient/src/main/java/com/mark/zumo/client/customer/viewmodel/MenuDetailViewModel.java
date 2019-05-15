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

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOptionCategory;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.customer.model.CartManager;
import com.mark.zumo.client.customer.model.CustomerMenuManager;
import com.mark.zumo.client.customer.model.CustomerOrderManager;
import com.mark.zumo.client.customer.model.CustomerSessionManager;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 24.
 */
public class MenuDetailViewModel extends AndroidViewModel {

    private static final String TAG = "MenuDetailViewModel";

    private final CustomerMenuManager customerMenuManager;
    private final CartManager cartManager;
    private final CustomerOrderManager customerOrderManager;
    private final CustomerSessionManager customerSessionManager;

    private final CompositeDisposable disposables;

    public MenuDetailViewModel(@NonNull final Application application) {
        super(application);
        customerMenuManager = CustomerMenuManager.INSTANCE;
        cartManager = CartManager.INSTANCE;
        customerOrderManager = CustomerOrderManager.INSTANCE;
        customerSessionManager = CustomerSessionManager.INSTANCE;

        disposables = new CompositeDisposable();
    }

    public LiveData<Menu> getMenu(String uuid) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        customerMenuManager.getMenuFromDisk(uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    public LiveData<OrderDetail> insertOrderDetailFromCart(String storeUuid, int cartIndex) {
        MutableLiveData<OrderDetail> liveData = new MutableLiveData<>();
        cartManager.getCart(storeUuid)
                .map(cart -> cart.getOrderDetail(cartIndex))
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    public LiveData<List<MenuOptionCategory>> getMenuOptionCategoryList(String menuUuid) {

        MutableLiveData<List<MenuOptionCategory>> liveData = new MutableLiveData<>();

        customerMenuManager.getCombinedMenuOptionCategoryListByMenuUuid(menuUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    public LiveData<OrderDetail> addToCartCurrentItems(String menuUuid, List<String> menuOptionUuidList, int amount) {
        MutableLiveData<OrderDetail> liveData = new MutableLiveData<>();
        customerMenuManager.createOrderDetail(menuUuid, menuOptionUuidList, amount)
                .flatMap(orderDetail ->
                        cartManager.getCart(orderDetail.storeUuid)
                                .firstElement()
                                .map(cart -> Maybe.fromAction(() -> cart.addCartItem(orderDetail)))
                                .map(cart -> orderDetail)
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnSuccess(liveData::setValue)
                .subscribe();
        return liveData;
    }

    public void updateToCartCurrentItems(String menuUuid, List<String> menuOptionUuidList, int amount, int cartIndex) {

        customerMenuManager.createOrderDetail(menuUuid, menuOptionUuidList, amount)
                .flatMap(orderDetail ->
                        cartManager.getCart(orderDetail.storeUuid)
                                .firstElement()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSuccess(cart -> cart.updateCartItem(orderDetail, cartIndex))
                ).doOnSubscribe(disposables::add)
                .subscribe();
    }

    public LiveData<MenuOrder> placeOrder(String menuUuid, List<String> menuOptionUuidList, int amount) {
        MutableLiveData<MenuOrder> liveData = new MutableLiveData<>();

        customerMenuManager.createOrderDetail(menuUuid, menuOptionUuidList, amount)
                .flatMap(customerOrderManager::createMenuOrder)
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
