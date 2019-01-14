/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.store.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.sign.store.fragment.registrationlist.StoreRegistrationListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 12. 26.
 */
public class StoreRegistrationFragment extends Fragment {

    @BindView(R.id.back) AppCompatImageView back;
    private StoreRegistrationListFragment storeRegistrationListFragment;

    public static StoreRegistrationFragment newInstance() {

        Bundle args = new Bundle();

        StoreRegistrationFragment fragment = new StoreRegistrationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_registration, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
        storeRegistrationListFragment = StoreRegistrationListFragment.newInstance()
                .doOnStoreRegistrationRequestSelected(this::onStoreRegistrationRequestSelected)
                .doOnNewRequestClicked(this::onClickNewRequest);

        StoreRegistrationCreateFragment storeRegistrationCreateFragment = createStoreRegistrationCreateFragment();

        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.store_registration_list_fragment, storeRegistrationListFragment)
                .replace(R.id.store_registration_console_fragment, storeRegistrationCreateFragment)
                .commit();

        boolean hasBackStack = getFragmentManager().getBackStackEntryCount() > 0;
        back.setVisibility(hasBackStack ? View.VISIBLE : View.GONE);
    }

    private void onClickNewRequest() {
        StoreRegistrationCreateFragment storeRegistrationCreateFragment = createStoreRegistrationCreateFragment();

        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.store_registration_console_fragment, storeRegistrationCreateFragment)
                .commit();
    }

    private StoreRegistrationCreateFragment createStoreRegistrationCreateFragment() {
        return StoreRegistrationCreateFragment.newInstance()
                .doOnCreateRequestSuccess(storeRegistrationListFragment::onStoreRegistrationComplete);
    }

    private void onStoreRegistrationRequestSelected(StoreRegistrationRequest storeRegistrationRequest) {

        StoreRegistrationDetailFragment storeRegistrationDetailFragment = ((StoreRegistrationDetailFragment) Fragment.instantiate(getContext(), StoreRegistrationDetailFragment.class.getName()))
                .setStoreRegistrationRequest(storeRegistrationRequest);

        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.store_registration_console_fragment, storeRegistrationDetailFragment)
                .commit();
    }

    @OnClick(R.id.back)
    public void onBackClicked() {
        getFragmentManager().popBackStack();
    }
}