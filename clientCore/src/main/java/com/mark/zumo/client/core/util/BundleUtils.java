/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.util;

import android.os.Bundle;

import java.util.Set;

/**
 * Created by mark on 18. 9. 23.
 */
public final class BundleUtils {

    public static boolean equalsBundles(Bundle a, Bundle b) {
        Set<String> aks = a.keySet();
        Set<String> bks = b.keySet();

        if (!aks.containsAll(bks)) {
            return false;
        }

        for (String key : aks) {
            if (!a.get(key).equals(b.get(key))) {
                return false;
            }
        }

        return true;
    }
}
