package com.mark.zumo.client.customer.device;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.mark.zumo.client.core.util.context.ContextHolder;

/**
 * Created by mark on 19. 5. 14.
 */
public enum CustomerVibrationController {
    INSTANCE;

    public static final int DURATION_MILLIS = 1000;
    private static final String TAG = "CustomerVibrationController";
    private final Context context;
    private Thread vibrationThread;

    CustomerVibrationController() {
        context = ContextHolder.getContext();
    }

    public void startVibratorThread() {
        Log.d(TAG, "startVibratorThread: ");
        vibrationThread = new VibratorThread(context);
        vibrationThread.start();
    }

    public void stopVibrationThread() {
        if (vibrationThread == null || !vibrationThread.isAlive()) {
            return;
        }

        Log.d(TAG, "stopVibrationThread: ");

        vibrationThread.interrupt();
        vibrationThread = null;
    }

    private class VibratorThread extends Thread {
        private Context context;

        private VibratorThread(final Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                synchronized (this) {
                    try {
                        createCompleteVibration(context);
                        wait(DURATION_MILLIS + 100);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "VibratorThread: interrupted");
                        return;
                    }
                }
            }
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
                vibrator.vibrate(VibrationEffect.createOneShot(DURATION_MILLIS, 255), audioAttributes);
            } else {
                vibrator.vibrate(DURATION_MILLIS);
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
