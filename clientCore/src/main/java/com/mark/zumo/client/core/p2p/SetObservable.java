package com.mark.zumo.client.core.p2p;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by mark on 18. 5. 1.
 */

class SetObservable<T> extends java.util.Observable implements Set<T> {

    final Set<T> set;

    SetObservable() {
        this.set = new HashSet<>();
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @NonNull
    @Override
    public Object[] toArray(@NonNull Object[] a) {
        return set.toArray(a);
    }

    @Override
    public boolean add(T o) {
        boolean added = set.add(o);
        if (added) {
            setChanged();
            notifyObservers();
        }
        return added;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = set.remove(o);
        if (removed) {
            setChanged();
            notifyObservers(null);
        }
        return removed;
    }

    @Override
    public boolean containsAll(@NonNull Collection c) {
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection c) {
        boolean addedAll = set.addAll(c);
        if (addedAll) {
            setChanged();
            notifyObservers(null);
        }
        return addedAll;
    }

    @Override
    public boolean retainAll(@NonNull Collection c) {
        boolean retainAll = set.retainAll(c);
        if (retainAll) {
            setChanged();
            notifyObservers(null);
        }
        return retainAll;
    }

    @Override
    public boolean removeAll(@NonNull Collection c) {
        boolean removeAll = set.removeAll(c);
        if (removeAll) {
            setChanged();
            notifyObservers(null);
        }
        return removeAll;
    }

    @Override
    public void clear() {
        set.clear();
        notifyObservers();
    }
}