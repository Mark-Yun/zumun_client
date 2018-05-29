package com.mark.zumo.client.customer.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.customer.model.CartManager;
import com.mark.zumo.client.customer.model.MenuManager;
import com.mark.zumo.client.customer.model.StoreManager;
import com.mark.zumo.client.customer.model.entity.Cart;
import com.mark.zumo.client.customer.model.entity.CartItem;

import java.text.NumberFormat;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 27.
 */
public class CartViewModel extends AndroidViewModel {

    public static final String TAG = "CartViewModel";

    private CartManager cartManager;
    private StoreManager storeManager;
    private MenuManager menuManager;

    private String currentStoreUuid;

    private CompositeDisposable disposables;

    public CartViewModel(@NonNull final Application application) {
        super(application);

        cartManager = CartManager.INSTANCE;
        storeManager = StoreManager.INSTANCE;
        menuManager = MenuManager.INSTANCE;

        disposables = new CompositeDisposable();
    }

    public LiveData<List<CartItem>> getCartItemList(String storeUuid) {
        currentStoreUuid = storeUuid;

        MutableLiveData<List<CartItem>> cartItemLiveData = new MutableLiveData<>();

        Disposable subscribe = cartManager.getCart(storeUuid)
                .map(Cart::getCartItemList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(cartItemLiveData::setValue)
                .subscribe();
        disposables.add(subscribe);

        return cartItemLiveData;
    }

    public void removeCartItem(int position) {
        Disposable subscribe = cartManager.getCart(currentStoreUuid)
                .firstElement()
                .doOnSuccess(cart -> cart.removeCartItem(position))
                .subscribe();

        disposables.add(subscribe);
    }

    public LiveData<Store> getStore(String storeUuid) {
        MutableLiveData<Store> storeLiveData = new MutableLiveData<>();

        Disposable subscribe = storeManager.getStoreFromDisk(storeUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(storeLiveData::setValue)
                .subscribe();

        disposables.add(subscribe);

        return storeLiveData;
    }

    public LiveData<Menu> getMenu(String menuUuid) {
        MutableLiveData<Menu> menuLiveData = new MutableLiveData<>();

        Disposable subscribe = menuManager.getMenuFromDisk(menuUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(menuLiveData::setValue)
                .subscribe();

        disposables.add(subscribe);

        return menuLiveData;
    }

    public LiveData<MenuOption> getMenuOption(String menuOptionUuid) {
        MutableLiveData<MenuOption> menuOptionLiveData = new MutableLiveData<>();

        Disposable subscribe = menuManager.getMenuOptionFromDisk(menuOptionUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(menuOptionLiveData::setValue)
                .subscribe();

        disposables.add(subscribe);

        return menuOptionLiveData;
    }

    public LiveData<String> getCartItemPriceLiveData(String storeUuid, int position) {
        MutableLiveData<String> liveData = new MutableLiveData<>();

        Disposable subscribe = cartManager.getCart(storeUuid)
                .map(cart -> cart.getCartItem(position))
                .firstElement()
                .flatMap(this::getCartItemPrice)
                .map(NumberFormat.getCurrencyInstance()::format)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .subscribe();

        disposables.add(subscribe);

        return liveData;
    }

    private Maybe<Integer> getCartItemPrice(CartItem cartItem) {
        return Maybe.just(cartItem)
                .flatMap(cartItem1 ->
                        Maybe.merge(
                                getOptionListPrice(cartItem1),
                                getMenuPrice(cartItem1)
                        ).reduce((integer, integer2) -> integer + integer2)
                                .map(integer -> integer * cartItem.getAmount())
                );
    }

    private Maybe<Integer> getOptionListPrice(CartItem cartItem) {
        return Observable.fromIterable(cartItem.getOrderDetailList())
                .map(orderDetail -> orderDetail.menuOptionUuid)
                .flatMap(menuManager::getMenuOptionFromDisk)
                .map(menuOption -> menuOption.price)
                .reduce((integer, integer2) -> integer + integer2)
                .subscribeOn(Schedulers.io());
    }

    private Maybe<Integer> getMenuPrice(CartItem cartItem) {
        return menuManager.getMenuFromDisk(cartItem.menuUuid)
                .map(menu -> menu.price)
                .firstElement()
                .subscribeOn(Schedulers.io());
    }

    public LiveData<String> getTotalPrice(String storeUuid) {

        MutableLiveData<String> liveData = new MutableLiveData<>();

        Disposable subscribe = cartManager.getCart(storeUuid)
                .doOnNext(cart ->
                        {
                            if (cart.getCartItemList().isEmpty()) {
                                liveData.postValue(NumberFormat.getCurrencyInstance().format(0));
                            } else {
                                Observable.fromIterable(cart.getCartItemList())
                                        .flatMapMaybe(this::getCartItemPrice)
                                        .reduce((integer, integer2) -> integer + integer2)
                                        .map(NumberFormat.getCurrencyInstance()::format)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnSuccess(liveData::setValue)
                                        .subscribe();
                            }
                        }
                ).subscribe();

        disposables.add(subscribe);
        return liveData;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
