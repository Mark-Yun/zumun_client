/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.view.BaseFragment;
import com.mark.zumo.client.store.R;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 10. 28.
 */
public class StoreProfileMainFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_split_2in1, container, false);
        ButterKnife.bind(this, view);
        inflateProfileMainFragment();
        return view;
    }

    private void inflateProfileMainFragment() {
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.left_fragment, new StoreProfileInfoFragment())
                .replace(R.id.right_fragment, new StoreProfilePreferenceFragment())
                .commitAllowingStateLoss();
    }
}
