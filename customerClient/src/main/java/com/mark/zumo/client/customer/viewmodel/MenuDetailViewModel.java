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
import android.util.Log;
import android.widget.Toast;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.model.CartManager;
import com.mark.zumo.client.customer.model.MenuManager;
import com.mark.zumo.client.customer.model.OrderManager;
import com.mark.zumo.client.customer.model.payment.PaymentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 24.
 */
public class MenuDetailViewModel extends AndroidViewModel {

    public static final String TAG = "MenuDetailViewModel";

    private MenuManager menuManager;
    private CartManager cartManager;
    private OrderManager orderManager;
    private PaymentManager paymentManager;

    private Map<String, List<MenuOption>> menuOptionMap;
    private Map<String, MenuOption> selectedOptionMap;
    private Map<String, MutableLiveData<MenuOption>> selectedOptionLiveDataMap;

    private CompositeDisposable disposables;

    private int amount;

    public MenuDetailViewModel(@NonNull final Application application) {
        super(application);
        menuManager = MenuManager.INSTANCE;
        cartManager = CartManager.INSTANCE;
        orderManager = OrderManager.INSTANCE;
        paymentManager = PaymentManager.INSTANCE;

        menuOptionMap = new LinkedHashMap<>();
        selectedOptionMap = new HashMap<>();
        selectedOptionLiveDataMap = new HashMap<>();

        disposables = new CompositeDisposable();
    }

    public LiveData<Menu> getMenu(String uuid) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();

        menuManager.getMenuFromDisk(uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(disposables::add)
                .subscribe();        

        return liveData;
    }

    public LiveData<Map<String, List<MenuOption>>> getMenuOptionMap(String menuUuid) {
        MutableLiveData<Map<String, List<MenuOption>>> liveData = new MutableLiveData<>();
        loadMenuOptions(liveData, menuUuid);
        return liveData;
    }

    private void loadMenuOptions(MutableLiveData<Map<String, List<MenuOption>>> liveData, String menuUuid) {
        menuOptionMap.clear();
        selectedOptionMap.clear();

        menuManager.getMenuOptionList(menuUuid)
                .flatMapSingle(Observable::toList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(menuOptions -> menuOptionMap.put(menuOptions.get(0).name, menuOptions))
                .doOnComplete(() -> liveData.postValue(menuOptionMap))
                .doOnSubscribe(disposables::add)
                .subscribe();        
    }

    public String getMenuAmount() {
        return String.valueOf(amount = 1);
    }

    public String increaseAmount() {
        return String.valueOf(++amount);
    }

    public String decreaseAmount() {
        return String.valueOf(amount > 1 ? --amount : amount);
    }

    public void selectMenuOption(MenuOption menuOption) {
        Log.d(TAG, "selectMenuOption: " + menuOption);
        selectedOptionMap.put(menuOption.name, menuOption);
        MutableLiveData<MenuOption> liveData = selectedOptionLiveDataMap.get(menuOption.name);
        liveData.setValue(menuOption);
    }

    public void deselectMenuOption(String key) {
        selectedOptionMap.remove(key);
        MutableLiveData<MenuOption> liveData = selectedOptionLiveDataMap.get(key);
        liveData.setValue(null);
    }

    public LiveData<MenuOption> getSelectedOption(String key) {
        MutableLiveData<MenuOption> liveData = selectedOptionLiveDataMap.get(key);
        if (liveData == null) {
            liveData = new MutableLiveData<>();
            selectedOptionLiveDataMap.put(key, liveData);
        }

        liveData.setValue(selectedOptionMap.get(key));
        return liveData;
    }

    public void addToCartCurrentItems(String storeUuid, Menu menu) {
        int price = menu.price;
        ArrayList<String> menuOptionUuidList = new ArrayList<>();
        for (MenuOption menuOption : selectedOptionMap.values()) {
            menuOptionUuidList.add(menuOption.uuid);
            price += menuOption.price;
        }

        OrderDetail orderDetail = new OrderDetail("", storeUuid, menu.uuid, menu.name, "", menuOptionUuidList, amount, price);
        cartManager.getCart(storeUuid)
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(cart -> cart.addCartItem(orderDetail))
                .doOnSuccess(cart -> selectedOptionMap.clear())
                .doOnSuccess(cart -> selectedOptionLiveDataMap.clear())
                .doOnSuccess(cart -> showAddToCartSucceedToast())
                .doOnSubscribe(disposables::add)
                .subscribe();
    }

    public LiveData<MenuOrder> placeOrder(String storeUuid, Menu menu) {
        MutableLiveData<MenuOrder> liveData = new MutableLiveData<>();

        ArrayList<String> menuOptionUuidList = new ArrayList<>();
        int price = menu.price;
        for (MenuOption menuOption : selectedOptionMap.values()) {
            menuOptionUuidList.add(menuOption.uuid);
            price += menuOption.price;
        }

        OrderDetail orderDetail = new OrderDetail("", storeUuid, menu.uuid, menu.name, "", menuOptionUuidList, amount, price);
        orderManager.createMenuOrder(orderDetail)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(unused -> selectedOptionMap.clear())
                .doOnNext(unused -> selectedOptionLiveDataMap.clear())
                .doOnNext(menuOrder -> Log.d(TAG, "placeOrder: success-" + menuOrder))
                .doOnNext(liveData::setValue)
                .doOnError(throwable -> Log.e(TAG, "placeOrder: ", throwable))
                .doOnSubscribe(disposables::add)
                .subscribe();

        return liveData;
    }

    private void showAddToCartSucceedToast() {
        Toast.makeText(ContextHolder.getContext(), R.string.toast_add_to_cart_item_succeed, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
