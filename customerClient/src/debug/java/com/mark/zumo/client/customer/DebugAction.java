/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.mark.zumo.client.core.util.context.ContextHolder;

/**
 * Created by mark on 18. 10. 7.
 */
public enum DebugAction {
    TEST_COMPLETE_VIBRATOR("Test Complete Vibration", DebugAction::createCompleteVibration);
    public final String buttonName;
    public final Runnable runnable;

    DebugAction(final String buttonName, final Runnable runnable) {
        this.buttonName = buttonName;
        this.runnable = runnable;
    }

    private static void createCompleteVibration() {
        Context context = ContextHolder.getContext();
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
}
