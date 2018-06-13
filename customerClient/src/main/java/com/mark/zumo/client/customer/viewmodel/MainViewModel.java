/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.customer.model.OrderManager;
import com.mark.zumo.client.customer.model.SessionManager;
import com.mark.zumo.client.customer.model.StoreManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 18. 5. 10.
 */
public class MainViewModel extends AndroidViewModel {

    private P2pClient p2pClient;
    private SessionManager sessionManager;
    private StoreManager storeManager;
    private OrderManager orderManager;

    private CompositeDisposable compositeDisposable;

    public MainViewModel(@NonNull final Application application) {
        super(application);

        p2pClient = P2pClient.INSTANCE;
        sessionManager = SessionManager.INSTANCE;
        storeManager = StoreManager.INSTANCE;
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<Store> findStore(Activity activity) {
        MutableLiveData<Store> liveData = new MutableLiveData<>();

        sessionManager.getSessionUser()
                .map(guestUser -> guestUser.uuid)
                .flatMap(sessionId -> p2pClient.findStore(activity, sessionId))
                .flatMapObservable(storeId -> storeManager.getStore(storeId))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<List<Menu>> requestMenuItemList(String storeUuid) {
        MutableLiveData<List<Menu>> liveData = new MutableLiveData<>();

        Maybe.just(storeUuid)
                .flatMap(p2pClient::requestMenuItems)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public void onSuccessPayment(String orderUuid) {
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        orderManager.getMenuOrderFromDisk(orderUuid)
                .map(this::createOrderNotification)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(notification -> notificationManager.notify(orderUuid.hashCode(), notification))
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();
    }

    @WorkerThread
    private Notification createOrderNotification(MenuOrder menuOrder) {
        int orderStateRes = MenuOrder.State.of(menuOrder.state).stringRes;
        String orderStateString = getApplication().getString(orderStateRes);
        Store store = storeManager.getStoreFromDisk(menuOrder.storeUuid).blockingGet();
        Bitmap iconBitmap = getBitmapFromURL(store.thumbnailUrl);

        return new Notification.Builder(getApplication())
                .setContentTitle(menuOrder.orderName)
                .setContentText(orderStateString)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(Icon.createWithBitmap(iconBitmap))
                .setOngoing(true)
                .build();
    }

    private Bitmap getBitmapFromURL(String stringUrl) {
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        p2pClient.clear();
    }
}
