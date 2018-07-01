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

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.main.MainActivity;

import java.util.Objects;

/**
 * Created by mark on 18. 6. 28.
 */
public enum NotificationHandler {
    INSTANCE;

    private Context context;
    private StoreManager storeManager;

    NotificationHandler() {
        context = ContextHolder.getContext();
        storeManager = StoreManager.INSTANCE;
    }

    public void requestOrderProgressNotification(@NonNull MenuOrder menuOrder) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChanel = createNotificationChanel(menuOrder);
            Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChanel);
        }

        storeManager.getStoreFromDisk(menuOrder.storeUuid)
                .map(store -> createOrderNotification(store, menuOrder))
                .doOnSuccess(notification -> notificationManager.notify(menuOrder.uuid.hashCode(), notification))
                .subscribe();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel createNotificationChanel(MenuOrder menuOrder) {
        String name = context.getString(R.string.order_progress_channel_name);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        return new NotificationChannel(menuOrder.uuid, name, importance);
    }

    private Notification createOrderNotification(Store store, MenuOrder menuOrder) {
        int orderStateRes = MenuOrder.State.of(menuOrder.state).stringRes;
        String orderStateString = context.getString(orderStateRes);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context, menuOrder.uuid)
                .setContentTitle(store.name + " - " + menuOrder.orderName)
                .setChannelId(menuOrder.uuid)
                .setContentText(orderStateString)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_order)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setContentIntent(activity)
                .build();
    }
}
