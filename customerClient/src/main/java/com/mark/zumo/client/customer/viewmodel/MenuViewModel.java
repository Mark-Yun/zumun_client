package com.mark.zumo.client.customer.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.customer.model.CartManager;
import com.mark.zumo.client.customer.model.MenuItemManager;
import com.mark.zumo.client.customer.model.StoreManager;
import com.mark.zumo.client.customer.model.UserManager;
import com.mark.zumo.client.customer.model.entity.Cart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 10.
 */
public class MenuViewModel extends AndroidViewModel {
    private MenuItemManager menuItemManager;
    private UserManager userManager;
    private CartManager cartManager;
    private StoreManager storeManager;

    private Map<String, MutableLiveData<Cart>> cartLiveDataMap;
    private MutableLiveData<Cart> currentCart;

    public MenuViewModel(@NonNull final Application application) {
        super(application);
        menuItemManager = MenuItemManager.INSTANCE;
        userManager = UserManager.INSTANCE;
        cartManager = CartManager.INSTANCE;
        storeManager = StoreManager.INSTANCE;

        cartLiveDataMap = new HashMap<>();
    }

    public LiveData<List<Menu>> getMenuItemList(Activity activity) {
        MutableLiveData<List<Menu>> listMutableLiveData = new MutableLiveData<>();
        userManager.getCurrentUser()
                .flatMap(customerUser -> menuItemManager.acquireMenuItem(activity, customerUser))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listMutableLiveData::setValue);

        return listMutableLiveData;
    }

    @Override
    protected void onCleared() {
        menuItemManager.clearClient();
    }

    public LiveData<Cart> getCart(String storeUuid) {
        if (!cartLiveDataMap.containsKey(storeUuid)) {
            MutableLiveData<Cart> cartLiveData = new MutableLiveData<>();
            Cart cart = cartManager.getCart(storeUuid);
            cartLiveData.setValue(cart);
            cartLiveDataMap.put(storeUuid, cartLiveData);
        }

        return currentCart = cartLiveDataMap.get(storeUuid);
    }

    public void addMenuToCart(Menu menu) {
        Cart cart = currentCart.getValue();
        cart.addMenu(menu);
        currentCart.setValue(cart);
    }

    public void removeMenuFromCart(int position) {
        Cart cart = currentCart.getValue();
        cart.removeMenu(position);
        currentCart.setValue(cart);
    }

    public void removeLatestMenuFromCart() {
        Cart cart = currentCart.getValue();
        cart.removeMenu(cart.getCartCount() - 1);
        currentCart.setValue(cart);
    }

    public LiveData<Store> getStore(String storeUuid) {
        MutableLiveData<Store> storeLiveData = new MutableLiveData<>();
        storeManager.getStore(storeUuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(storeLiveData::setValue);

        return storeLiveData;
    }
}
