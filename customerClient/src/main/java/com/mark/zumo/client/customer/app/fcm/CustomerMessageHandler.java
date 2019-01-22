/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.app.fcm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mark.zumo.client.core.appserver.request.message.MessageFactory;
import com.mark.zumo.client.core.appserver.request.message.OrderAcceptedMessage;
import com.mark.zumo.client.core.appserver.request.message.OrderCompleteMessage;
import com.mark.zumo.client.core.appserver.request.message.SnsMessage;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.model.CustomerOrderManager;
import com.mark.zumo.client.customer.model.NotificationHandler;

import java.util.Map;

/**
 * Created by mark on 18. 9. 21.
 */
public enum CustomerMessageHandler {
    INSTANCE;

    public static final String TAG = "CustomerMessageHandler";

    private NotificationHandler notificationHandler;
    private CustomerOrderManager customerOrderManager;

    private Thread vibrationThread;
    private String orderUuid;
    private BroadcastReceiver broadcastReceiver;

    private boolean isVibrating;

    CustomerMessageHandler() {
        notificationHandler = NotificationHandler.INSTANCE;
        customerOrderManager = CustomerOrderManager.INSTANCE;
    }

    private void registerBroadCastReceiver(Context context) {
        Log.d(TAG, "registerBroadCastReceiver: ");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.d(TAG, "onReceive: " + intent.getAction());

                if (orderUuid != null && orderUuid.equals(intent.getStringExtra(VibrationContract.ORDER_KEY))) {
                    stopVibrationThread(context);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VibrationContract.ACTION);
        context.registerReceiver(broadcastReceiver, intentFilter, VibrationContract.PERMISSION, new Handler(Looper.getMainLooper()));
    }

    void handleMessage(Context context, Map<String, String> data) {
        SnsMessage snsMessage = MessageFactory.create(data);
        if (snsMessage == null) {
            Log.e(TAG, "handleMessage: message is NULL!");
            return;
        }

        if (snsMessage instanceof OrderAcceptedMessage) {
            onOrderAccepted(context, (OrderAcceptedMessage) snsMessage);
            sendOrderUpdated();
        } else if (snsMessage instanceof OrderCompleteMessage) {
            onOrderComplete(context, (OrderCompleteMessage) snsMessage);
            sendOrderUpdated();
        }
    }

    private void sendOrderUpdated() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ContextHolder.getContext());
        localBroadcastManager.sendBroadcast(new Intent(OrderRepository.ACTION_ORDER_UPDATED));
    }

    private void onOrderAccepted(Context context, OrderAcceptedMessage message) {
        Log.d(TAG, "onOrderAccepted: " + message);
        customerOrderManager.getMenuOrderFromApi(message.orderUuid)
                .doOnSuccess(menuOrder -> notificationHandler.requestOrderProgressNotification(context, menuOrder))
                .subscribe();
    }

    private void onOrderComplete(Context context, OrderCompleteMessage message) {
        Log.d(TAG, "onOrderComplete: " + message);
        customerOrderManager.getMenuOrderFromApi(message.orderUuid)
                .doOnSuccess(menuOrder -> notificationHandler.requestOrderProgressNotification(context, menuOrder))
                .subscribe();

        orderUuid = message.orderUuid;
        setUpVibratorThread(context);
        registerBroadCastReceiver(context);
    }

    private void setUpVibratorThread(Context context) {
        Log.d(TAG, "setUpVibratorThread: ");
        vibrationThread = new VibratorThread(context);
        isVibrating = true;
        vibrationThread.start();
    }

    private void stopVibrationThread(Context context) {
        Log.d(TAG, "stopVibrationThread: isVibrating=" + isVibrating);
        if (!isVibrating) {
            return;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(orderUuid.hashCode());

        context.unregisterReceiver(broadcastReceiver);
        isVibrating = false;
        if (vibrationThread == null) {
            return;
        }
        vibrationThread.interrupt();
    }

    private void createCompleteVibration(final Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= 26) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            vibrator.vibrate(VibrationEffect.createOneShot(3000, 255), audioAttributes);
        } else {
            vibrator.vibrate(3000);
        }
    }

    public interface VibrationContract {
        String ACTION = "com.mark.zumo.client.customer.action.STOP_VIBRATION";
        String PERMISSION = "com.mark.zumo.client.customer.permission.ORDER_PROGRESS_NOTIFICATION";
        String ORDER_KEY = "order_uuid";
    }

    private class VibratorThread extends Thread {
        private Context context;

        private VibratorThread(final Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            super.run();
            while (isVibrating) {
                createCompleteVibration(context);
                synchronized (this) {
                    try {
                        wait(4000);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "VibratorThread: interrupted");
                    }
                }
            }
        }

        @Override
        public void interrupt() {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator == null || !vibrator.hasVibrator()) {
                return;
            }

            vibrator.cancel();
            super.interrupt();
        }
    }
}
