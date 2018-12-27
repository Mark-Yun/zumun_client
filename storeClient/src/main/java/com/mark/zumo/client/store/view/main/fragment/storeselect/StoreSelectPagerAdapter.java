/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.main.fragment.storeselect;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mark.zumo.client.core.entity.user.store.StoreUserContract;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by mark on 18. 12. 27.
 */
class StoreSelectPagerAdapter extends FragmentStatePagerAdapter {

    private final List<StoreUserContract> storeUserContractList;
    private final StoreSelectListener listener;
    private final Context context;

    StoreSelectPagerAdapter(final Fragment fragment, final StoreSelectListener listener) {
        super(fragment.getFragmentManager());

        this.context = fragment.getContext();
        this.listener = listener;
        storeUserContractList = new CopyOnWriteArrayList<>();
    }

    void setStoreUserContractList(List<StoreUserContract> storeUserContractList) {
        this.storeUserContractList.clear();
        this.storeUserContractList.addAll(storeUserContractList);

        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(final int position) {
        StoreUserContract storeUserContract = storeUserContractList.get(position);
        StoreSelectCardFragment fragment = (StoreSelectCardFragment) StoreSelectCardFragment.instantiate(context, StoreSelectCardFragment.class.getName());
        return fragment.setStoreUuid(storeUserContract.storeUuid)
                .setListener(listener);
    }

    @Override
    public int getCount() {
        return this.storeUserContractList.size();
    }

    interface StoreSelectListener extends StoreSelectCardFragment.StoreSelectListener {
    }
}
