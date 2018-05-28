package com.mark.zumo.client.customer.model.entity;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import com.mark.zumo.client.core.util.context.ContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.ObservableEmitter;

/**
 * Created by mark on 18. 5. 22.
 */
public class Cart {

    public static final String TAG = "Cart";
    private List<CartItem> cartItemList;
    private Collection<ObservableEmitter<Cart>> emitterCollection;

    public Cart() {
        cartItemList = new ArrayList<>();
        emitterCollection = new ArrayList<>();
    }

    public void addCartItem(CartItem cartItem) {
        Log.d(TAG, "addCartItem: " + cartItem);
        cartItemList.add(cartItem);
        notifyOnNext();
        vibrationFeedback();
    }

    public Cart addEmitter(ObservableEmitter<Cart> emitter) {
        emitterCollection.add(emitter);
        return this;
    }

    private void notifyOnNext() {
        for (ObservableEmitter<Cart> emitter : emitterCollection) {
            if (emitter != null) {
                emitter.onNext(Cart.this);
            }
        }
    }

    public CartItem getCartItem(int position) {
        return cartItemList.get(position);
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void clear() {
        cartItemList.clear();
    }

    public void removeCartItem(int position) {
        Log.d(TAG, "removeCartItem: " + position);
        cartItemList.remove(position);
        notifyOnNext();
        vibrationFeedback();
    }

    public void removeLatestCartItem() {
        removeCartItem(cartItemList.size() - 1);
        notifyOnNext();
    }

    public int getItemCount() {
        return cartItemList.size();
    }

    private void vibrationFeedback() {
        Vibrator vibrator = (Vibrator) ContextHolder.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }
}
