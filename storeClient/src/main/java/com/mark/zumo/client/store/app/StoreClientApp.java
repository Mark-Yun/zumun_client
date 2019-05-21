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

package com.mark.zumo.client.store.app;

import android.app.Application;
import android.util.Log;

import com.mark.zumo.client.core.app.AppErrorHandler;
import com.mark.zumo.client.core.aws.AWSMobileClientRepository;
import com.mark.zumo.client.core.util.context.ContextInjector;
import com.mark.zumo.client.store.model.StorePrinterManager;

/**
 * Created by mark on 18. 5. 7.
 */

public class StoreClientApp extends Application {

    public static final String TAG = "StoreClientApp";

    @Override
    public void onCreate() {
        super.onCreate();

        ContextInjector.inject(this);
        AppErrorHandler.setup();

        StorePrinterManager.INSTANCE.fetchConnectPairedBluetoothPrinter(this);
    }
}
