/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.fragment.profile.StoreProfileMainFragment;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 7. 13.
 */
public class SettingMainFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_main, container, false);
        ButterKnife.bind(this, view);
        inflateDefaultFragment();
        return view;
    }

    private void inflateDefaultFragment() {
        Fragment settingPreferenceFragment = Fragment.instantiate(getActivity(), SettingPreferenceFragment.class.getName());
        Fragment storeProfileFragment = Fragment.instantiate(getActivity(), StoreProfileMainFragment.class.getName());

        getFragmentManager().beginTransaction()
                .replace(R.id.preference_fragment, settingPreferenceFragment)
                .replace(R.id.setting_main_fragment, storeProfileFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
