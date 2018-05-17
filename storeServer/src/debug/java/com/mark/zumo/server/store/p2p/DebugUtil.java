package com.mark.zumo.server.store.p2p;

import android.os.Build;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;

/**
 * Created by mark on 18. 5. 3.
 */

class DebugUtil {

    static GuestUser testCustomerUser() {
        return new GuestUser();
    }

    static Store testStore() {
        return new Store("TEST", Build.MODEL, 0, 0);
    }

    static MenuOrder testOrder() {
        return new MenuOrder(1, 2, 3, null, 5, 100);
    }
}
