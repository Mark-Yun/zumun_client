/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.core.repository.StoreRepository;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.device.CustomerVibrationController;
import com.mark.zumo.client.customer.view.order.detail.OrderDetailActivity;

import java.text.NumberFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 6. 28.
 */
public enum CustomerNotificationManager {
    INSTANCE;

    public static final String TAG = "CustomerNotificationManager";

    private static final String CHANNEL_ID_ORDER_PROGRESS = "order_progress";

    private static final String ACTION_STOP_VIBRATION = "com.mark.zumo.client.customer.STOP_VIBRATION";
    private static final String KEY_ORDER_UUID = MenuOrder.Schema.uuid;

    private final Context context;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final CustomerVibrationController customerVibrationController;
    private final Map<String, Disposable> disposableMap;

    CustomerNotificationManager() {
        context = ContextHolder.getContext();
        storeRepository = StoreRepository.INSTANCE;
        orderRepository = OrderRepository.INSTANCE;
        customerVibrationController = CustomerVibrationController.INSTANCE;
        disposableMap = new ConcurrentHashMap<>();

        registerBroadCastReceiver(context);
    }

    private void registerBroadCastReceiver(Context context) {
        Log.d(TAG, "registerBroadCastReceiver: ");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();
                Log.d(TAG, "onReceive: " + action);

                switch (action) {
                    case ACTION_STOP_VIBRATION:
                        String orderUuid = intent.getStringExtra(KEY_ORDER_UUID);
                        stopOrderNotificationTracking(orderUuid);
                        cancelOrderNotification(context, orderUuid);
                }

            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STOP_VIBRATION);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void startOrderNotificationTracking(final MenuOrder menuOrder) {
        Log.d(TAG, "startOrderNotificationTracking: " + menuOrder);
        orderRepository.getMenuOrderObservable(menuOrder.uuid)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::onOrderUpdated)
                .doOnSubscribe(disposable -> disposableMap.put(menuOrder.uuid, disposable))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void onOrderUpdated(final MenuOrder menuOrder) {
        MenuOrder.State state = MenuOrder.State.of(menuOrder.state);

        if (MenuOrder.State.CREATED.equals(state) || MenuOrder.State.REQUESTED.equals(state)) {
            return;
        }
        Log.d(TAG, "onOrderUpdated: " + menuOrder);

        requestOrderProgressNotification(context, menuOrder);

        if (MenuOrder.State.COMPLETE.equals(state)) {
            customerVibrationController.startVibratorThread();

        } else if (MenuOrder.State.FINISHED.equals(state)
                || MenuOrder.State.REJECTED.equals(state)
                || MenuOrder.State.CANCELED.equals(state)) {
            stopOrderNotificationTracking(menuOrder.uuid);
        }
    }

    public void stopOrderNotificationTracking(final String menuOrderUuid) {
        cancelOrderNotification(context, menuOrderUuid);
        customerVibrationController.stopVibrationThread();

        if (disposableMap.containsKey(menuOrderUuid)) {
            disposableMap.remove(menuOrderUuid).dispose();
        }
    }

    private void cancelOrderNotification(final Context context, final String menuOrderUuid) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(menuOrderUuid.hashCode());
    }

    public void requestOrderProgressNotification(final Context context, @NonNull MenuOrder menuOrder) {
        Log.d(TAG, "requestOrderProgressNotification: " + menuOrder);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChanel = createNotificationChanel();
            notificationManager.createNotificationChannel(notificationChanel);
        }

        storeRepository.getStoreFromApi(menuOrder.storeUuid)
                .map(store -> createOrderNotification(store, menuOrder))
                .doOnSuccess(notification -> notificationManager.notify(menuOrder.uuid.hashCode(), notification))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel createNotificationChanel() {
        String name = context.getString(R.string.order_progress_channel_name);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        return new NotificationChannel(CHANNEL_ID_ORDER_PROGRESS, name, importance);
    }

    private Notification createOrderNotification(Store store, MenuOrder menuOrder) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.KEY_ORDER_UUID, menuOrder.uuid);
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        MenuOrder.State state = MenuOrder.State.of(menuOrder.state);
        boolean isComplete = state == MenuOrder.State.COMPLETE;
        String stateString = context.getString(state.notificationContentRes);
        String contentText = isComplete
                ? context.getString(R.string.order_number_description) + menuOrder.orderNumber
                : NumberFormat.getCurrencyInstance().format(menuOrder.totalPrice);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, menuOrder.uuid)
                .setContentTitle("[" + store.name + "] " + menuOrder.orderName + " " + stateString)
                .setChannelId(CHANNEL_ID_ORDER_PROGRESS)
                .setAutoCancel(true)
                .setPriority(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? NotificationManager.IMPORTANCE_MAX : Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(contentText)
                .setTicker(contentText)
                .setOngoing(isComplete)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_order)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setContentIntent(activity);

        if (isComplete) {
            Intent turnAlarmOffIntent = new Intent(ACTION_STOP_VIBRATION);
            turnAlarmOffIntent.putExtra(KEY_ORDER_UUID, menuOrder.uuid);

            PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, turnAlarmOffIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action = new NotificationCompat.Action(0, context.getString(R.string.turn_vibration_off_button_text), broadcast);
            builder.addAction(action)
                    .setFullScreenIntent(activity, true);
        }

        return builder.build();
    }
}
