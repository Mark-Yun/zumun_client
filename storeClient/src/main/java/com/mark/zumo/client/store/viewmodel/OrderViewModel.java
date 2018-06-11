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
import com.mark.zumo.client.store.model.MenuManager;
import com.mark.zumo.client.store.model.OrderManager;
import com.mark.zumo.client.store.model.entity.OrderBucket;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 16.
 */
public class OrderViewModel extends AndroidViewModel {

    private static final String TAG = "OrderViewModel";

    private OrderManager orderManager;
    private MenuManager menuManager;

    private CompositeDisposable compositeDisposable;

    public OrderViewModel(@NonNull final Application application) {
        super(application);

        orderManager = OrderManager.INSTANCE;
        menuManager = MenuManager.INSTANCE;

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<MenuOrder>> acceptedMenuOrderList() {
        MutableLiveData<List<MenuOrder>> liveData = new MutableLiveData<>();
        orderManager.acceptedOrderBucket()
                .map(OrderBucket::getOrderList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuOrder>> requestedMenuOrderList() {
        MutableLiveData<List<MenuOrder>> liveData = new MutableLiveData<>();
        orderManager.requestedOrderBucket()
                .map(OrderBucket::getOrderList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<OrderDetail>> orderDetailList(String orderUuid) {
        MutableLiveData<List<OrderDetail>> liveData = new MutableLiveData<>();
        orderManager.getOrderDetailList(orderUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuOption>> menuOptionList(List<String> menuOptionUuid) {
        MutableLiveData<List<MenuOption>> liveData = new MutableLiveData<>();
        menuManager.getMenuOptionList(menuOptionUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<MenuOrder> acceptOrder(MenuOrder menuOrder) {
        MutableLiveData<MenuOrder> liveData = new MutableLiveData<>();
        orderManager.acceptOrder(menuOrder)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSuccess(order -> showToast("accepted " + order.name))
                .subscribe();
        showToast("acceptOrder");

        return liveData;
    }

    private void showToast(final String acceptOrder) {
        Toast.makeText(getApplication(), acceptOrder, Toast.LENGTH_SHORT).show();
    }

    public void acceptAllOrder() {
        orderManager.acceptAllOrder()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(count -> showToast("accepted " + count + "orders."))
                .subscribe();
    }

    public void rejectOrder(MenuOrder menuOrder) {
        showToast("rejectOrder");
    }

    public void completeOrder(MenuOrder menuOrder) {
        showToast("completeOrder" + menuOrder.uuid);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
    }
}
