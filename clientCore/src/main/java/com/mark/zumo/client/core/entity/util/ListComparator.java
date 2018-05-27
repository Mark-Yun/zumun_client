package com.mark.zumo.client.core.entity.util;

import java.util.List;

import io.reactivex.functions.BiPredicate;

/**
 * Created by mark on 18. 5. 23.
 */
public class ListComparator<T> implements BiPredicate<List<T>, List<T>> {
    @Override
    public boolean test(final List<T> list, final List<T> list2) throws Exception {
        if (list.size() != list2.size()) return false;
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).toString().equals(list2.get(i).toString())) return false;
        }
        return true;
    }
}