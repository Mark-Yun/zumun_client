package com.mark.zumo.client.core.util.context;

import android.content.Context;

/**
 * Created by mark on 18. 5. 10.
 */
public final class ContextHolder {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    static void setContext(final Context context) {
        ContextHolder.context = context;
    }
}
