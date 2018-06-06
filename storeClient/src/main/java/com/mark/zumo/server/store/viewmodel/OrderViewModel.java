/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.server.store.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.server.store.model.MenuOrderManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 16.
 */
public class OrderViewModel extends AndroidViewModel {

    public static final String TAG = "OrderViewModel";
    private MenuOrderManager menuOrderManager;

    private List<MenuOrder> requestedMenuOrderList;
    private CompositeDisposable compositeDisposable;

    public OrderViewModel(@NonNull final Application application) {
        super(application);

        menuOrderManager = MenuOrderManager.INSTANCE;

        requestedMenuOrderList = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();
    }

    private void onMenuOrderRequested(MutableLiveData<List<MenuOrder>> liveData, MenuOrder menuOrder) {
        requestedMenuOrderList.add(menuOrder);
        liveData.setValue(requestedMenuOrderList);
    }

    public LiveData<List<MenuOrder>> acceptedMenuOrderList() {
        MutableLiveData<List<MenuOrder>> liveData = new MutableLiveData<>();
        menuOrderManager.getAcceptedMenuOrderList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuOrder>> requestedMenuOrderList() {
        MutableLiveData<List<MenuOrder>> liveData = new MutableLiveData<>();
        menuOrderManager.getRequestedMenuOrderList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(menuOrder -> onMenuOrderRequested(liveData, menuOrder))
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<OrderDetail>> orderDetailList(String orderUuid) {
        MutableLiveData<List<OrderDetail>> liveData = new MutableLiveData<>();
        menuOrderManager.getOrderDetailList(orderUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public LiveData<List<MenuOption>> menuOptionList(List<String> menuOptionUuid) {
        MutableLiveData<List<MenuOption>> liveData = new MutableLiveData<>();
        menuOrderManager.getMenuOptionList(menuOptionUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
        return liveData;
    }

    public void acceptOrder(MenuOrder menuOrder) {
        Toast.makeText(ContextHolder.getContext(), "acceptOrder", Toast.LENGTH_SHORT).show();
    }

    public void rejectOrder(MenuOrder menuOrder) {
        Toast.makeText(ContextHolder.getContext(), "rejectOrder", Toast.LENGTH_SHORT).show();
    }

    public void completeOrder(MenuOrder menuOrder) {
        Toast.makeText(ContextHolder.getContext(), "completeOrder" + menuOrder.uuid, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
    }
}
