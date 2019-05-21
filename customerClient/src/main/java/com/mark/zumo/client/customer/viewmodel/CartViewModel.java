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
import com.mark.zumo.client.core.database.entity.MenuOption;
import com.mark.zumo.client.core.database.entity.MenuOrder;
import com.mark.zumo.client.core.database.entity.OrderDetail;
import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.customer.model.CartManager;
import com.mark.zumo.client.customer.model.CustomerMenuManager;
import com.mark.zumo.client.customer.model.CustomerOrderManager;
import com.mark.zumo.client.customer.model.CustomerSessionManager;
import com.mark.zumo.client.customer.model.CustomerStoreManager;
import com.mark.zumo.client.customer.model.entity.Cart;

import java.text.NumberFormat;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 27.
 */
public class CartViewModel extends AndroidViewModel {

    private static final String TAG = "CartViewModel";

    private final CartManager cartManager;
    private final CustomerStoreManager customerStoreManager;
    private final CustomerMenuManager customerMenuManager;
    private final CustomerOrderManager customerOrderManager;
    private final CustomerSessionManager customerSessionManager;

    private final CompositeDisposable disposables;

    public CartViewModel(@NonNull final Application application) {
        super(application);

        cartManager = CartManager.INSTANCE;
        customerStoreManager = CustomerStoreManager.INSTANCE;
        customerMenuManager = CustomerMenuManager.INSTANCE;
        customerOrderManager = CustomerOrderManager.INSTANCE;
        customerSessionManager = CustomerSessionManager.INSTANCE;

        disposables = new CompositeDisposable();
    }

    public LiveData<List<OrderDetail>> getCartItemList(String storeUuid) {
        MutableLiveData<List<OrderDetail>> cartItemLiveData = new MutableLiveData<>();

        cartManager.getCart(storeUuid)
                .map(Cart::getOrderDetailList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(cartItemLiveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return cartItemLiveData;
    }

    public void removeCartItem(String storeUuid, int position) {
        cartManager.getCart(storeUuid)
                .firstElement()
                .doOnSuccess(cart -> cart.removeCartItem(position))
                .doOnSubscribe(disposables::add)
                .subscribe();
    }

    public void clearCartItem(String storeUuid) {
        cartManager.clearCart(storeUuid);
    }

    public LiveData<Store> getStore(String storeUuid) {
        MutableLiveData<Store> storeLiveData = new MutableLiveData<>();

        customerStoreManager.getStoreObservableFromDisk(storeUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(storeLiveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return storeLiveData;
    }

    public LiveData<Menu> getMenu(String menuUuid) {
        MutableLiveData<Menu> menuLiveData = new MutableLiveData<>();

        customerMenuManager.getMenuFromDisk(menuUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(menuLiveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return menuLiveData;
    }

    public LiveData<MenuOption> getMenuOption(String menuOptionUuid) {
        MutableLiveData<MenuOption> menuOptionLiveData = new MutableLiveData<>();

        customerMenuManager.getMenuOptionFromDisk(menuOptionUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(menuOptionLiveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return menuOptionLiveData;
    }

    public LiveData<String> getCartItemPriceLiveData(String storeUuid, int position) {
        MutableLiveData<String> liveData = new MutableLiveData<>();

        cartManager.getCart(storeUuid)
                .map(cart -> cart.getOrderDetail(position))
                .firstElement()
                .flatMap(this::getCartItemPrice)
                .map(NumberFormat.getCurrencyInstance()::format)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    private Maybe<Integer> getCartItemPrice(OrderDetail orderDetail) {
        return Maybe.just(orderDetail)
                .flatMap(cartItem1 ->
                        Maybe.merge(
                                getOptionListPrice(cartItem1),
                                getMenuPrice(cartItem1)
                        ).reduce((integer, integer2) -> integer + integer2)
                                .map(integer -> integer * orderDetail.quantity)
                );
    }

    private Maybe<Integer> getOptionListPrice(OrderDetail orderDetail) {
        return Observable.fromIterable(orderDetail.menuOptionUuidList)
                .flatMapMaybe(customerMenuManager::getMenuOptionFromDisk)
                .map(menuOption -> menuOption.price)
                .reduce((integer, integer2) -> integer + integer2)
                .doOnSubscribe(disposables::add)
                .subscribeOn(Schedulers.io());
    }

    private Maybe<Integer> getMenuPrice(OrderDetail orderDetail) {
        return customerMenuManager.getMenuFromDisk(orderDetail.menuUuid)
                .map(menu -> menu.price)
                .subscribeOn(Schedulers.io());
    }

    public LiveData<String> getTotalPrice(String storeUuid) {

        MutableLiveData<String> liveData = new MutableLiveData<>();

        cartManager.getCart(storeUuid)
                .doOnNext(cart ->
                        {
                            if (cart.getOrderDetailList().isEmpty()) {
                                liveData.postValue(NumberFormat.getCurrencyInstance().format(0));
                            } else {
                                Observable.fromIterable(cart.getOrderDetailList())
                                        .flatMapMaybe(this::getCartItemPrice)
                                        .reduce((integer, integer2) -> integer + integer2)
                                        .map(NumberFormat.getCurrencyInstance()::format)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnSuccess(liveData::setValue)
                                        .doOnSubscribe(disposables::add)
                                        .subscribe();
                            }
                        }
                ).subscribe();
        return liveData;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    public LiveData<MenuOrder> placeOrder(String storeUuid) {
        MutableLiveData<MenuOrder> liveData = new MutableLiveData<>();

        customerSessionManager.getSessionUser()
                .flatMapObservable(ignored -> cartManager.getCart(storeUuid))
                .firstElement()
                .filter(cart -> cart.getTotalAmount() > 0)
                .map(Cart::getOrderDetailList)
                .flatMap(customerOrderManager::createMenuOrder)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }
}
