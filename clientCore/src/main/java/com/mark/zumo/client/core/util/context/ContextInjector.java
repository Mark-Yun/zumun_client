/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.util.context;

import android.app.Application;

/**
 * Created by mark on 18. 5. 10.
 */
public final class ContextInjector {
    public static void inject(Application application) {
        ContextHolder.setContext(application);
    }
}
