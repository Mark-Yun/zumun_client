package com.mark.zumo.client.customer.p2p;

import android.os.Build;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.CustomerUser;

/**
 * Created by mark on 18. 5. 3.
 */

public class DebugUtil {

    public static CustomerUser testCustomerUser() {
        return new CustomerUser(0, Build.MODEL, 0);
    }

    public static Store testStore() {
        return new Store(5, Build.MODEL, 0, 0, 31, 31);
    }
}
