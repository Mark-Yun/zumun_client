package com.mark.zumo.client.core.entity.util;

import io.reactivex.functions.BiPredicate;

/**
 * Created by mark on 18. 5. 23.
 */
public class EntityComparator<T> implements BiPredicate<T, T> {
    @Override
    public boolean test(final T t, final T t2) throws Exception {
        return t.toString().equals(t2.toString());
    }
}
