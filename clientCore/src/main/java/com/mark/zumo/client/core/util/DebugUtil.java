package com.mark.zumo.client.core.util;

import android.os.Build;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mark on 18. 5. 3.
 */

public class DebugUtil {

    public static GuestUser guestUser() {
        return new GuestUser();
    }

    public static Store store() {
        return new Store("TEST", Build.MODEL, 0, 0);
    }

    public static MenuOrder menuOrder() {
        return new MenuOrder(1, 2, 3, null, 5, 100);
    }

    public static List<Store> storeList() {
        Store[] stores = {
                new Store("uuid", "test1", 15, 15),
                new Store("uuid", "testStore2", 15, 15),
                new Store("uuid", "testTestStore3", 15, 15),
                new Store("uuid", "COCOCOCOCOCOCO_@#!@$*I@", 15, 15),
                new Store("uuid", "LAIIEJ!F#IFJASDJFP(!O@$I!P(", 15, 15),
        };
        return new ArrayList<>(Arrays.asList(stores));
    }
}
