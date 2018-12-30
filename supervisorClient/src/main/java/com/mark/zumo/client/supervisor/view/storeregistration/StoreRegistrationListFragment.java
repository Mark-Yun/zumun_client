/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.supervisor.view.storeregistration;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.supervisor.R;
import com.mark.zumo.client.supervisor.view.storeregistration.detail.StoreRegistrationRequestDetailActivity;
import com.mark.zumo.client.supervisor.viewmodel.StoreRegistrationViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 12. 30.
 */
public class StoreRegistrationListFragment extends Fragment {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private StoreRegistrationViewModel storeRegistrationViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storeRegistrationViewModel = ViewModelProviders.of(this).get(StoreRegistrationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_registration_list, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        StoreRegistrationListAdapter storeRegistrationListAdapter = new StoreRegistrationListAdapter(this::onSelectStoreRegistrationRequest);
        recyclerView.setAdapter(storeRegistrationListAdapter);

        storeRegistrationViewModel.getStoreRegistrationRequest().observe(this, storeRegistrationListAdapter::setStoreRegistrationRequestList);
    }

    private void onSelectStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest) {
        StoreRegistrationRequestDetailActivity.start(getContext(), storeRegistrationRequest.uuid);
    }
}
