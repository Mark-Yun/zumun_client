/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.p2p.observable;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;

import java.util.Observable;

/**
 * Created by mark on 18. 5. 3.
 */

public class EndpointInfoObservable extends Observable {
    private DiscoveredEndpointInfo discoveredEndpointInfo;

    public void setDiscoveredEndpointInfo(DiscoveredEndpointInfo discoveredEndpointInfo) {
        this.discoveredEndpointInfo = discoveredEndpointInfo;
        setChanged();
        notifyObservers();
    }

    public DiscoveredEndpointInfo getDiscoveredEndpointInfo() {
        return discoveredEndpointInfo;
    }
}
