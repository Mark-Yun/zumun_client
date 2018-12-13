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

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.store.model.MenuOptionManager;
import com.mark.zumo.client.store.model.StoreMenuManager;
import com.mark.zumo.client.store.model.StoreOrderManager;
import com.mark.zumo.client.store.model.StoreSessionManager;
import com.mark.zumo.client.store.model.entity.OrderBucket;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 16.
 */
public class OrderViewModel extends AndroidViewModel {

    private static final String TAG = "OrderViewModel";

    private final StoreOrderManager storeOrderManager;
    private final StoreMenuManager storeMenuManager;
    private final StoreSessionManager storeSessionManager;
    private final MenuOptionManager menuOptionManager;

    private final CompositeDisposable compositeDisposable;

    public OrderViewModel(@NonNull final Application application) {
        super(application);

        storeOrderManager = StoreOrderManager.INSTANCE;
        storeMenuManager = StoreMenuManager.INSTANCE;
        storeSessionManager = StoreSessionManager.INSTANCE;
        menuOptionManager = MenuOptionManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<MenuOrder>> canceledMenuOrderList() {
        MutableLiveData<List<MenuOrder>> liveData = new MutableLiveData<>();
        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(storeOrderManager::canceledOrderBucket)
                .map(OrderBucket::getOrderList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuOrder>> completeMenuOrderList() {
        MutableLiveData<List<MenuOrder>> liveData = new MutableLiveData<>();
        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(storeOrderManager::completeOrderBucket)
                .map(OrderBucket::getOrderList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuOrder>> requestedMenuOrderList() {
        MutableLiveData<List<MenuOrder>> liveData = new MutableLiveData<>();
        storeSessionManager.getSessionStore()
                .map(store -> store.uuid)
                .flatMapObservable(storeOrderManager::requestedOrderBucket)
                .map(OrderBucket::getOrderList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<MenuOrder> getMenuOrderFromDisk(String menuOrderUuid) {
        MutableLiveData<MenuOrder> liveData = new MutableLiveData<>();
        storeOrderManager.getMenuOrderFromDisk(menuOrderUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<OrderDetail>> orderDetailList(String orderUuid) {
        MutableLiveData<List<OrderDetail>> liveData = new MutableLiveData<>();
        storeOrderManager.getOrderDetailList(orderUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuOption>> menuOptionList(List<String> menuOptionUuid) {
        MutableLiveData<List<MenuOption>> liveData = new MutableLiveData<>();
        menuOptionManager.getMenuOptionList(menuOptionUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<MenuOrder> acceptOrder(String orderUuid) {
        MutableLiveData<MenuOrder> liveData = new MutableLiveData<>();
        storeOrderManager.getMenuOrderFromDisk(orderUuid)
                .flatMap(storeOrderManager::acceptOrder)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSuccess(order -> showToast("accepted " + order.orderName))
                .subscribe();

        return liveData;
    }

    public LiveData<MenuOrder> refundOrder(String orderUuid) {
        MutableLiveData<MenuOrder> liveData = new MutableLiveData<>();
        return liveData;
    }

    public LiveData<MenuOrder> completeOrder(String orderUuid) {
        MutableLiveData<MenuOrder> liveData = new MutableLiveData<>();
        storeOrderManager.completeOrder(orderUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSuccess(order -> showToast("complete " + order.orderName))
                .subscribe();
        return liveData;
    }

    private void showToast(final String acceptOrder) {
        Toast.makeText(getApplication(), acceptOrder, Toast.LENGTH_SHORT).show();
    }

    public void acceptAllOrder() {
        storeOrderManager.acceptAllOrder()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(count -> showToast("accepted " + count + "orders."))
                .subscribe();
    }

    public LiveData<MenuOrder> rejectOrder(String orderUuid) {
        MutableLiveData<MenuOrder> liveData = new MutableLiveData<>();
        storeOrderManager.rejectOrder(orderUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSuccess(order -> showToast("rejected " + order.orderName))
                .subscribe();
        return liveData;
    }

    public void completeOrder(MenuOrder menuOrder) {
        showToast("completeOrder" + menuOrder.uuid);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
    }
}
