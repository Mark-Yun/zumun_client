package com.mark.zumo.client.customer.p2p;

import android.os.Build;

import com.mark.zumo.client.core.entity.Order;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.CustomerUser;

/**
 * Created by mark on 18. 5. 3.
 */

class DebugUtil {

    static CustomerUser testCustomerUser() {
        return new CustomerUser(0, Build.MODEL, 0);
    }

    static Store testStore() {
        return new Store(5, Build.MODEL, 0, 0, 31, 31);
    }

    static Order testOrder() {
        return new Order(1, 2, 3, null, 5, 100);
    }
}
