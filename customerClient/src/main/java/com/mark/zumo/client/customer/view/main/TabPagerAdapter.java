package com.mark.zumo.client.customer.view.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.view.menu.MenuFragment;
import com.mark.zumo.client.customer.view.place.PlaceFragment;

/**
 * Created by mark on 18. 5. 18.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private static final String[] FRAGMENTS = {
            MenuFragment.class.getName(),
            PlaceFragment.class.getName()
    };

    TabPagerAdapter(final FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(final int position) {
        return Fragment.instantiate(ContextHolder.getContext(), FRAGMENTS[position]);
    }

    @Override
    public int getCount() {
        return FRAGMENTS.length;
    }
}
