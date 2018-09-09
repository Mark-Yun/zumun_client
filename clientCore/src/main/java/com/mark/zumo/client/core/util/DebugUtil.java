/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.util;

import android.os.Build;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;

import java.util.UUID;

/**
 * Created by mark on 18. 5. 3.
 */

public class DebugUtil {

    private static final String TEST_STORE_UUID = "FD8BC4DD00B04E60A166A5FBD3454E8F";

    public static GuestUser guestUser() {
        return new GuestUser(UUID.randomUUID().toString());
    }

    public static Store store() {
        return new Store(TEST_STORE_UUID, Build.MODEL, 37.2624914, 127.0446137, "TEST", "TEST", "", "", "");
    }

    public static MenuOrder menuOrder() {
        return new MenuOrder(UUID.randomUUID().toString(),
                "TESTNAME",
                TEST_STORE_UUID,
                TEST_STORE_UUID,
                "24", System.currentTimeMillis(), 5, 25500, 0);
    }

}
