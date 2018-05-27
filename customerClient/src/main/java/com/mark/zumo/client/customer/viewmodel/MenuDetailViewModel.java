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
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.model.CartManager;
import com.mark.zumo.client.customer.model.MenuManager;
import com.mark.zumo.client.customer.model.entity.CartItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 24.
 */
public class MenuDetailViewModel extends AndroidViewModel {

    public static final String TAG = "MenuDetailViewModel";

    private MenuManager menuManager;
    private CartManager cartManager;

    private Map<String, List<MenuOption>> menuOptionMap;
    private Map<String, MenuOption> selectedOptionMap;
    private Map<String, MutableLiveData<MenuOption>> selectedOptionLiveDataMap;

    public MenuDetailViewModel(@NonNull final Application application) {
        super(application);
        menuManager = MenuManager.INSTANCE;
        cartManager = CartManager.INSTANCE;

        menuOptionMap = new LinkedHashMap<>();
        selectedOptionMap = new HashMap<>();
        selectedOptionLiveDataMap = new HashMap<>();
    }

    public LiveData<Menu> getMenu(String uuid) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();
        menuManager.getMenu(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(liveData::setValue);
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

        menuManager.getMenuOptions(menuUuid)
                .flatMapSingle(Observable::toList)
                .doOnNext(menuOptions -> Log.d(TAG, "loadMenuOptions: " + menuOptions.size()))
                .doOnNext(menuOptions -> menuOptionMap.put(menuOptions.get(0).name, menuOptions))
                .doOnComplete(() -> liveData.postValue(menuOptionMap))
                .subscribe();
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

    public void addToCartCurrentItems(String storeUuid) {
        CartItem cartItem = CartItem.fromOptionMenu(storeUuid, from(selectedOptionMap.values()));
        cartManager.getCart(storeUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(cart -> cart.addCartItem(cartItem))
                .doOnNext(cart -> selectedOptionMap.clear())
                .doOnNext(cart -> selectedOptionLiveDataMap.clear())
                .doOnNext(cart -> showAddToCartSucceedToast())
                .subscribe();
    }

    private void showAddToCartSucceedToast() {
        Toast.makeText(ContextHolder.getContext(), R.string.toast_add_to_cart_item_succeed, Toast.LENGTH_SHORT).show();
    }

    private Collection<OrderDetail> from(Collection<MenuOption> optionSet) {
        ArrayList<OrderDetail> orderDetailSet = new ArrayList<>();
        for (MenuOption option : optionSet) {
            orderDetailSet.add(new OrderDetail(null, option.menuUuid, null, option.uuid));
        }
        return orderDetailSet;
    }
}
