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

package com.mark.zumo.server.store.app;

import android.app.Application;

import com.mark.zumo.client.core.util.context.ContextInjector;
import com.wonderkiln.blurkit.BlurKit;

/**
 * Created by mark on 18. 5. 7.
 */

public class StoreServerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextInjector.inject(this);
        BlurKit.init(this);
    }
}
