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
import com.mark.zumo.client.store.view.main.fragment.storeselect.StoreSelectFragment;

/**
 * Created by mark on 19. 1. 14.
 */
public class StoreSelectMainFragment extends Fragment {

    private StoreSelectFragment.StoreSelectListener listener;
    private Runnable storeRegistrationAction;

    public static StoreSelectMainFragment newInstance() {

        Bundle args = new Bundle();

        StoreSelectMainFragment fragment = new StoreSelectMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_select_main, container, false);
        inflateView();
        return view;
    }

    private void inflateView() {
        StoreSelectFragment storeSelectFragment = StoreSelectFragment.newInstance()
                .onSelectStore(listener::onSelectStore)
                .onClickStoreRegistration(storeRegistrationAction);

        getFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, storeSelectFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public StoreSelectMainFragment onSelectStore(final StoreSelectFragment.StoreSelectListener listener) {
        this.listener = listener;
        return this;
    }

    public StoreSelectMainFragment onClickStoreRegistration(Runnable storeRegistrationAction) {
        this.storeRegistrationAction = storeRegistrationAction;
        return this;
    }

}
