package com.mark.zumo.server.store.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.server.store.model.MenuOrderManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by mark on 18. 5. 16.
 */
public class OrderViewModel extends AndroidViewModel {

    private List<MenuOrder> menuOrderList = new ArrayList<>();
    private int currentOrderIndex;
    private MenuOrderManager menuOrderManager;
    private MutableLiveData<Integer> currentPositionLiveData = new MutableLiveData<>();

    public OrderViewModel(@NonNull final Application application) {
        super(application);

        menuOrderManager = MenuOrderManager.INSTANCE;
    }

    private void bindMenuOrderManager(MutableLiveData<List<MenuOrder>> liveData) {
        menuOrderManager.getMenuOrder()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(menuOrder -> onMenuOrderAdded(liveData, menuOrder))
                .subscribe();
    }

    private void onMenuOrderAdded(MutableLiveData<List<MenuOrder>> liveData, MenuOrder menuOrder) {
        menuOrderList.add(menuOrder);
        liveData.setValue(menuOrderList);
    }

    public LiveData<Integer> currentOrderPosition() {
        return currentPositionLiveData;
    }

    private void updateCurrentOrder() {
        currentPositionLiveData.setValue(currentOrderIndex);
    }

    public LiveData<List<MenuOrder>> menuOrderList() {
        MutableLiveData<List<MenuOrder>> menuOrderListLiveData = new MutableLiveData<>();
        bindMenuOrderManager(menuOrderListLiveData);
        return menuOrderListLiveData;
    }

    public void acceptOrder(MenuOrder menuOrder) {
        Toast.makeText(ContextHolder.getContext(), "acceptOrder", Toast.LENGTH_SHORT).show();
    }

    public void rejectOrder(MenuOrder menuOrder) {
        Toast.makeText(ContextHolder.getContext(), "rejectOrder", Toast.LENGTH_SHORT).show();
    }

    public void completeOrder(MenuOrder menuOrder) {
        Toast.makeText(ContextHolder.getContext(), "completeOrder", Toast.LENGTH_SHORT).show();
        nextOrder();
    }

    private void setCurrentOrder(int position) {

    }

    private boolean nextOrder() {
        if (menuOrderList.size() <= currentOrderIndex + 1) {
            return false;
        }

        currentOrderIndex += 1;
        updateCurrentOrder();
        return true;
    }

    private boolean prevOrder() {
        if (0 <= currentOrderIndex - 1) {
            return false;
        }

        currentOrderIndex -= 1;
        updateCurrentOrder();
        return true;
    }
}
