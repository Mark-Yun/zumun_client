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
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.app.fcm.CustomerMessageHandler;
import com.mark.zumo.client.customer.view.order.detail.OrderDetailActivity;

import java.text.NumberFormat;
import java.util.Objects;

/**
 * Created by mark on 18. 6. 28.
 */
public enum NotificationHandler {
    INSTANCE;

    public static final String TAG = "NotificationHandler";
    public static final String CHANNEL_ID_ORDER_PROGRESS = "order_progress";
    private Context context;
    private CustomerStoreManager customerStoreManager;

    NotificationHandler() {
        context = ContextHolder.getContext();
        customerStoreManager = CustomerStoreManager.INSTANCE;
    }

    public void requestOrderProgressNotification(final Context context, @NonNull MenuOrder menuOrder) {
        Log.d(TAG, "requestOrderProgressNotification: " + menuOrder);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChanel = createNotificationChanel();
            Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChanel);
        }

        customerStoreManager.getStoreFromDisk(menuOrder.storeUuid)
                .map(store -> createOrderNotification(store, menuOrder))
                .doOnSuccess(notification -> notificationManager.notify(menuOrder.uuid.hashCode(), notification))
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
            Intent turnAlarmOffIntent = new Intent(CustomerMessageHandler.VibrationContract.ACTION);
            turnAlarmOffIntent.putExtra(CustomerMessageHandler.VibrationContract.ORDER_KEY, menuOrder.uuid);

            PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, turnAlarmOffIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action = new NotificationCompat.Action(0, context.getString(R.string.turn_vibration_off_button_text), broadcast);
            builder.addAction(action)
                    .setFullScreenIntent(activity, true);
        }

        return builder.build();
    }
}
