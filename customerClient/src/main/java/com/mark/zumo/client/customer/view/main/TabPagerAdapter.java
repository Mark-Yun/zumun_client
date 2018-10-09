/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.customer.view.menu.MenuFragment;
import com.mark.zumo.client.customer.view.order.OrderFragment;
import com.mark.zumo.client.customer.view.permission.PermissionFragment;
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
    private static final String TAG = "TabPagerAdapter";

    private final SparseArray<Fragment> fragmentList;
    private FindingStoreFragment.StoreFindListener storeFindListener;

    TabPagerAdapter(final FragmentManager fragmentManager) {
        super(fragmentManager);
        fragmentList = new SparseArray<>();
    }

    private static boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(ContextHolder.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    void setFindListener(FindingStoreFragment.StoreFindListener listener) {
        this.storeFindListener = listener;
    }

    @Override
    public Fragment getItem(final int position) {
        if (fragmentList.get(position) == null) {
            if (position <= 1 && !isLocationPermissionGranted()) {
                Fragment fragment = PermissionFragment.instantiate(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this::onGrantSuccess);
                fragmentList.put(position, fragment);
            } else {
                Fragment fragment = Fragment.instantiate(ContextHolder.getContext(), FRAGMENTS_NAME[position]);
                if (fragment instanceof FindingStoreFragment) {
                    ((FindingStoreFragment) fragment).setStoreFindListener(storeFindListener);
                }
                fragmentList.put(position, fragment);
            }
        }

        return fragmentList.get(position);
    }

    private void onGrantSuccess() {
        fragmentList.remove(0);
        fragmentList.remove(1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (fragmentList.indexOfValue((Fragment) object) < 1) {
            return POSITION_NONE;
        } else {
            return super.getItemPosition(object);
        }
    }

    public void onD2dLoadStore(Store store) {
        Bundle bundle = new Bundle();
        bundle.putString(MenuFragment.KEY_STORE_UUID, store.uuid);
        Fragment fragment = Fragment.instantiate(ContextHolder.getContext(), MenuFragment.class.getName(), bundle);
        fragmentList.remove(0);
        fragmentList.put(0, fragment);
        fragment.setEnterTransition(new Fade(Fade.IN));
        fragment.setExitTransition(new Fade(Fade.OUT));
        try {
            notifyDataSetChanged();
        } catch (IllegalStateException e) {
            Log.e(TAG, "onD2dLoadStore: ", e);
        }
    }

    @Override
    public int getCount() {
        return FRAGMENTS_NAME.length;
    }
}
