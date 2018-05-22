package com.mark.zumo.client.customer.view.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.view.menu.MenuFragment;
import com.mark.zumo.client.customer.view.place.PlaceFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mark on 18. 5. 18.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private static final String[] FRAGMENTS_NAME = {
            MenuFragment.class.getName(),
            PlaceFragment.class.getName()
    };

    private Map<Integer, Fragment> fragmentList = new HashMap<>();

    TabPagerAdapter(final FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(final int position) {
        if (!fragmentList.containsKey(position)) {
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
