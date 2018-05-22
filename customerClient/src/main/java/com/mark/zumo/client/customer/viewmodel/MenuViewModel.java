package com.mark.zumo.client.customer.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.customer.model.CartManager;
import com.mark.zumo.client.customer.model.MenuItemManager;
import com.mark.zumo.client.customer.model.UserManager;
import com.mark.zumo.client.customer.model.entity.Cart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by mark on 18. 5. 10.
 */
public class MenuViewModel extends AndroidViewModel {
    private MenuItemManager menuItemManager;
    private UserManager userManager;
    private Disposable disposable;
    private CartManager cartManager;

    private Map<UUID, MutableLiveData<Cart>> cartLiveDataMap;
    private MutableLiveData<Cart> currentCart;

    public MenuViewModel(@NonNull final Application application) {
        super(application);
        menuItemManager = MenuItemManager.INSTANCE;
        userManager = UserManager.INSTANCE;
        cartManager = CartManager.INSTANCE;

        cartLiveDataMap = new HashMap<>();
    }

    public LiveData<List<Menu>> getMenuItemList(Activity activity) {
        MutableLiveData<List<Menu>> listMutableLiveData = new MutableLiveData<>();
        disposable = userManager.getCurrentUser()
                .flatMapSingle(customerUser -> menuItemManager.acquireMenuItem(activity, customerUser))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listMutableLiveData::setValue);

        return listMutableLiveData;
    }

    @Override
    protected void onCleared() {
        disposable.dispose();
        menuItemManager.clearClient();
    }

    public LiveData<Cart> getCart(UUID storeUuid) {
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
}
