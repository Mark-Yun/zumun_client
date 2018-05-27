package com.mark.zumo.client.customer.model.entity;

import android.content.Context;
import android.os.Vibrator;

import com.mark.zumo.client.core.util.context.ContextHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;

/**
 * Created by mark on 18. 5. 22.
 */
public class Cart {

    private List<CartItem> cartItemList;
    private ObservableEmitter<Cart> emitter;

    public Cart(ObservableEmitter<Cart> emitter) {
        cartItemList = new ArrayList<>();
        this.emitter = emitter;
    }

    public void addCartItem(CartItem cartItem) {
        cartItemList.add(cartItem);
        emitter.onNext(this);
        vibrationFeedback();
    }


    public void removeCartItem(int position) {
        cartItemList.remove(position);
        emitter.onNext(this);
        vibrationFeedback();
    }

    public void removeLatestCartItem() {
        removeCartItem(cartItemList.size() - 1);
        emitter.onNext(this);
    }

    public int getCartCount() {
        return cartItemList.size();
    }

    private void vibrationFeedback() {
        Vibrator vibrator = (Vibrator) ContextHolder.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(5);
    }
}
