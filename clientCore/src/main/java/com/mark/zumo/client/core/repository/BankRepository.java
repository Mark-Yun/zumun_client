/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import android.os.Bundle;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;

/**
 * Created by mark on 19. 1. 13.
 */
public enum BankRepository {
    INSTANCE;

    private static final String TAG = "MenuRepository";

    private static Bundle session;
    private static BankRepository sInstance;

    private final DiskRepository diskRepository;

    BankRepository() {
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
    }

    private NetworkRepository networkRepository() {
        return AppServerServiceProvider.INSTANCE.networkRepository();
    }
}
