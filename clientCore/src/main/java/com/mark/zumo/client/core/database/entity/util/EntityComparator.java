/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.database.entity.util;

import io.reactivex.functions.BiPredicate;

/**
 * Created by mark on 18. 5. 23.
 */
public class EntityComparator<T> implements BiPredicate<T, T> {
    @Override
    public boolean test(final T t, final T t2) {
        return t.toString().equals(t2.toString());
    }
}
