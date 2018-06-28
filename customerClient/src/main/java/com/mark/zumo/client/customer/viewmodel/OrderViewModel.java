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

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.customer.model.MenuManager;
import com.mark.zumo.client.customer.model.OrderManager;
import com.mark.zumo.client.customer.model.SessionManager;
import com.mark.zumo.client.customer.model.StoreManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 12.
 */
public class OrderViewModel extends AndroidViewModel {
    private static final String TAG = "OrderViewModel";

    private final OrderManager orderManager;
    private final StoreManager storeManager;
    private final SessionManager sessionManager;

    private final CompositeDisposable compositeDisposable;
    private final MenuManager menuManager;

    public OrderViewModel(@NonNull final Application application) {
        super(application);

        orderManager = OrderManager.INSTANCE;
        storeManager = StoreManager.INSTANCE;
        sessionManager = SessionManager.INSTANCE;
        menuManager = MenuManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<Store> getStoreData(String storeUuid) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();

        storeManager.getStore(storeUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<List<MenuOrder>> getMenuOrderList() {
        MutableLiveData<List<MenuOrder>> liveData = new MutableLiveData<>();

        sessionManager.getSessionUser()
                .map(guestUser -> guestUser.uuid)
                .flatMapObservable(orderManager::getMenuOrderListByCustomerUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<MenuOrder> getMenuOrder(String orderUuid) {
        MutableLiveData<MenuOrder> liveData = new MutableLiveData<>();

        orderManager.getMenuOrderFromDisk(orderUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<List<OrderDetail>> getMenuOrderDetail(String orderUuid) {
        MutableLiveData<List<OrderDetail>> liveData = new MutableLiveData<>();

        orderManager.getOrderDetailListByOrderUuid(orderUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        MutableLiveData<List<MenuOption>> liveData = new MutableLiveData<>();
        menuManager.getMenuOptionList(menuOptionUuidList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }
}
