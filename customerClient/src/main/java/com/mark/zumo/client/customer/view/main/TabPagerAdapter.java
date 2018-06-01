/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.view.menu.MenuFragment;
import com.mark.zumo.client.customer.view.order.OrderFragment;
import com.mark.zumo.client.customer.view.place.PlaceFragment;

/**
 * Created by mark on 18. 5. 18.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private static final String[] FRAGMENTS_NAME = {
            MenuFragment.class.getName(),
            PlaceFragment.class.getName(),
            OrderFragment.class.getName()
    };

    private SparseArray<Fragment> fragmentList = new SparseArray<>();

    TabPagerAdapter(final FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(final int position) {
        if (fragmentList.get(position) == null) {
            Fragment fragment = Fragment.instantiate(ContextHolder.getContext(), FRAGMENTS_NAME[position]);
            fragmentList.put(position, fragment);
        }

        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return FRAGMENTS_NAME.length;
    }
}
