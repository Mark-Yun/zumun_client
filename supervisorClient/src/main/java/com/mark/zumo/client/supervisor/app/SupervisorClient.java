/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.supervisor.app;

import android.app.Application;

import com.mark.zumo.client.core.app.AppErrorHandler;
import com.mark.zumo.client.core.util.context.ContextInjector;

/**
 * Created by mark on 18. 12. 30.
 */
public class SupervisorClient extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextInjector.inject(this);
        AppErrorHandler.setup();
    }
}
