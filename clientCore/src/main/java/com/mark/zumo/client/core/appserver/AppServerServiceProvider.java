package com.mark.zumo.client.core.appserver;

/**
 * Created by mark on 18. 4. 30.
 */

public enum AppServerServiceProvider {
    INSTANCE;

    public final AppServerService service;

    AppServerServiceProvider() {
        service = AppServerService.retrofit.create(AppServerService.class);
    }
}
