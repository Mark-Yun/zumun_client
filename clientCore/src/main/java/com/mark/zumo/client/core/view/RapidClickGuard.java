/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.view;

import android.os.SystemClock;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mark on 18. 10. 4.
 */
public final class RapidClickGuard {
    private static Map<View, Long> lastClickedTimeMap = new HashMap<>();

    public static boolean shouldBlock(View view, long millis) {
        long currentTime = SystemClock.elapsedRealtime();
        if (!lastClickedTimeMap.containsKey(view)) {
            lastClickedTimeMap.put(view, currentTime);
            return false;
        }

        long lastClickedTime = lastClickedTimeMap.remove(view);
        if (currentTime - lastClickedTime > millis) {
            lastClickedTimeMap.put(view, currentTime);
            return false;
        }

        return true;
    }
}
