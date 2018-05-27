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
import com.mark.zumo.client.customer.model.MenuManager;
import com.mark.zumo.client.customer.model.StoreManager;
import com.mark.zumo.client.customer.model.UserManager;
import com.mark.zumo.client.customer.model.entity.Cart;
import com.mark.zumo.client.customer.model.entity.CartItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 10.
 */
public class MenuViewModel extends AndroidViewModel {

    private MenuManager menuManager;
    private UserManager userManager;
    private CartManager cartManager;
    private StoreManager storeManager;

    private Map<String, MutableLiveData<Cart>> cartLiveDataMap;
    private String currentStoreUuid;

    public MenuViewModel(@NonNull final Application application) {
        super(application);
        menuManager = MenuManager.INSTANCE;
        userManager = UserManager.INSTANCE;
        cartManager = CartManager.INSTANCE;
        storeManager = StoreManager.INSTANCE;

        cartLiveDataMap = new HashMap<>();
    }

    public LiveData<List<Menu>> getMenuItemList(Activity activity) {
        MutableLiveData<List<Menu>> listMutableLiveData = new MutableLiveData<>();
        userManager.getCurrentUser()
                .flatMap(customerUser -> menuManager.acquireMenuItem(activity, customerUser))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listMutableLiveData::setValue);

        return listMutableLiveData;
    }

    @Override
    protected void onCleared() {
        menuManager.clearClient();
    }

    public LiveData<Cart> getCart(String storeUuid) {
        if (!cartLiveDataMap.containsKey(storeUuid)) {
            MutableLiveData<Cart> cartLiveData = new MutableLiveData<>();
            cartLiveDataMap.put(storeUuid, cartLiveData);
        }

        currentStoreUuid = storeUuid;
        MutableLiveData<Cart> currentCart = cartLiveDataMap.get(currentStoreUuid);

        cartManager.getCart(storeUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(currentCart::setValue)
                .subscribe();

        return currentCart;
    }

    public LiveData<Boolean> addMenuToCart(Menu menu) {
        MutableLiveData<Boolean> cartItemLiveData = new MutableLiveData<>();

        cartManager.getCart(currentStoreUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(cart -> cart.addCartItem(CartItem.fromMenu(menu)))
                .doOnNext(cart -> cartItemLiveData.setValue(true))
                .subscribe();

        return cartItemLiveData;
    }

    public void removeLatestMenuFromCart() {
        cartManager.getCart(currentStoreUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(Cart::removeLatestCartItem)
                .subscribe();
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
