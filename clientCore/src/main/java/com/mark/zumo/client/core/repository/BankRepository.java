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
import com.mark.zumo.client.core.util.BundleUtils;

/**
 * Created by mark on 19. 1. 13.
 */
public class BankRepository {

    private static final String TAG = "MenuRepository";

    private static Bundle session;
    private static BankRepository sInstance;

    private final DiskRepository diskRepository;
    private final NetworkRepository networkRepository;

    private BankRepository(final Bundle session) {
        networkRepository = AppServerServiceProvider.INSTANCE.buildNetworkRepository(session);
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
        BankRepository.session = session;
    }

    public static BankRepository getInstance(Bundle session) {
        if (sInstance == null || !BundleUtils.equalsBundles(BankRepository.session, session)) {
            synchronized (BankRepository.class) {
                if (sInstance == null) {
                    sInstance = new BankRepository(session);
                }
            }
        }

        return sInstance;
    }

}
