/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.main.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.sign.store.fragment.StoreRegistrationFragment;

/**
 * Created by mark on 19. 1. 14.
 */
public class StoreRegistrationMainFragment extends Fragment {

    public static StoreRegistrationMainFragment newInstance() {

        Bundle args = new Bundle();

        StoreRegistrationMainFragment fragment = new StoreRegistrationMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_registration_main, container, false);
        inflateView();
        return view;
    }

    private void inflateView() {
        StoreRegistrationFragment storeRegistrationFragment = StoreRegistrationFragment.newInstance();

        getFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, storeRegistrationFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }


}
