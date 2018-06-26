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

package com.mark.zumo.client.core.view;

import android.content.Context;
import android.os.Vibrator;

import com.mark.zumo.client.core.util.context.ContextHolder;

/**
 * Created by mark on 18. 6. 3.
 */
public final class TouchResponse {


    private static final int LEVEL_SMALL = 5;
    private static final int LEVEL_MEDIUM = 10;
    private static final int LEVEL_BIG = 100;

    private static void touchResponse(long level) {
        getVibrator().vibrate(level);
    }

    public static void small() {
        touchResponse(LEVEL_SMALL);
    }

    public static void medium() {
        touchResponse(LEVEL_MEDIUM);
    }

    public static void big() {
        touchResponse(LEVEL_BIG);
    }

    private static Vibrator getVibrator() {
        Context context = ContextHolder.getContext();
        return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }
}
