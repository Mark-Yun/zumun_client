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
import com.mark.zumo.client.customer.model.CustomerMenuManager;
import com.mark.zumo.client.customer.model.CustomerNotificationManager;
import com.mark.zumo.client.customer.model.CustomerOrderManager;
import com.mark.zumo.client.customer.model.CustomerSessionManager;
import com.mark.zumo.client.customer.model.CustomerStoreManager;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 6. 12.
 */
public class OrderViewModel extends AndroidViewModel {
    private static final String TAG = "OrderViewModel";

    private final CustomerOrderManager customerOrderManager;
    private final CustomerStoreManager customerStoreManager;
    private final CustomerSessionManager customerSessionManager;
    private final CustomerNotificationManager customerNotificationManager;

    private final CompositeDisposable compositeDisposable;
    private final CustomerMenuManager customerMenuManager;

    public OrderViewModel(@NonNull final Application application) {
        super(application);

        customerOrderManager = CustomerOrderManager.INSTANCE;
        customerStoreManager = CustomerStoreManager.INSTANCE;
        customerSessionManager = CustomerSessionManager.INSTANCE;
        customerMenuManager = CustomerMenuManager.INSTANCE;
        customerNotificationManager = CustomerNotificationManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<Store> getStoreData(String storeUuid) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();

        customerStoreManager.getStore(storeUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<List<MenuOrder>> getMenuOrderList() {
        MutableLiveData<List<MenuOrder>> liveData = new MutableLiveData<>();

        customerSessionManager.getSessionUser()
                .map(guestUser -> guestUser.uuid)
                .flatMapObservable(customerOrderManager::getMenuOrderListByCustomerUuid)
                .doOnNext(menuOrderList ->
                        Observable.fromIterable(menuOrderList)
                                .distinct()
                                .filter(order -> MenuOrder.State.of(order.state) != MenuOrder.State.CREATED)
                                .sorted((o1, o2) -> (int) (o2.createdDate - o1.createdDate))
                                .toList().toObservable()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(liveData::setValue)
                                .doOnSubscribe(compositeDisposable::add)
                                .subscribe()
                ).doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<MenuOrder> getMenuOrder(String orderUuid) {
        MutableLiveData<MenuOrder> liveData = new MutableLiveData<>();

        customerOrderManager.getMenuOrderObservable(orderUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<List<OrderDetail>> getMenuOrderDetail(String orderUuid) {
        MutableLiveData<List<OrderDetail>> liveData = new MutableLiveData<>();

        customerOrderManager.getOrderDetailListByOrderUuid(orderUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<List<MenuOption>> getMenuOptionList(List<String> menuOptionUuidList) {
        MutableLiveData<List<MenuOption>> liveData = new MutableLiveData<>();
        customerMenuManager.getMenuOptionList(menuOptionUuidList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public void stopOrderAlarm(final String menuOrderUuid) {
        customerNotificationManager.stopOrderNotificationTracking(menuOrderUuid);
    }
}
