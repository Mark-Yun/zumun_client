/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.p2p;

import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.Strategy;
import com.mark.zumo.client.core.app.BuildConfig;

/**
 * Created by mark on 18. 5. 1.
 */

class Options {
    static final String SERVICE_ID = "com.mark.zumun" + BuildConfig.BUILD_TYPE.name();

    private static final Strategy P2P_CONNECTION_STRATEGY = Strategy.P2P_STAR;
    static final AdvertisingOptions ADVERTISING = new AdvertisingOptions.Builder()
            .setStrategy(P2P_CONNECTION_STRATEGY)
            .build();
    static final DiscoveryOptions DISCOVERY = new DiscoveryOptions.Builder()
            .setStrategy(P2P_CONNECTION_STRATEGY)
            .build();
    private static final int P2P_MESSAGE_STRATEGY = com.google.android.gms.nearby.messages.Strategy.DISTANCE_TYPE_DEFAULT;

}
