/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.main;

import android.os.Bundle;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.view.menu.MenuFragment;
import com.mark.zumo.client.customer.view.order.OrderFragment;
import com.mark.zumo.client.customer.view.place.PlaceFragment;

/**
 * Created by mark on 18. 5. 18.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private static final String[] FRAGMENTS_NAME = {
            FindingStoreFragment.class.getName(),
            PlaceFragment.class.getName(),
            OrderFragment.class.getName()
    };

    private SparseArray<Fragment> fragmentList;

    TabPagerAdapter(final FragmentManager fragmentManager) {
        super(fragmentManager);
        fragmentList = new SparseArray<>();
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
    public int getItemPosition(Object object) {
        if (fragmentList.indexOfValue((Fragment) object) < 1) {
            return POSITION_NONE;
        } else {
            return super.getItemPosition(object);
        }
    }

    public void onD2dLoadStore(Store store) {
        Bundle bundle = new Bundle();
        bundle.putString(MenuFragment.KEY_STORE_UUID, store.uuid);
        bundle.putBoolean(MenuFragment.KEY_IS_D2D, true);
        Fragment fragment = Fragment.instantiate(ContextHolder.getContext(), MenuFragment.class.getName(), bundle);
        fragmentList.remove(0);
        fragmentList.put(0, fragment);
        fragment.setEnterTransition(new Fade(Fade.IN));
        fragment.setExitTransition(new Fade(Fade.OUT));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return FRAGMENTS_NAME.length;
    }
}
