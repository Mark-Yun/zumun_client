/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.store.fragment.registrationlist;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.StoreRegistrationViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 12. 26.
 */
public class StoreRegistrationListFragment extends Fragment {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.request_id) AppCompatTextView requestId;
    @BindView(R.id.store_name) AppCompatTextView storeName;
    @BindView(R.id.status) AppCompatTextView result;
    @BindView(R.id.created_date) AppCompatTextView createdDate;

    private StoreRegistrationRequestSelectListener listener;
    private Runnable newRequestAction;

    private StoreRegistrationViewModel storeRegistrationViewModel;
    private StoreRegistrationRequestAdapter storeRegistrationRequestAdapter;

    public static StoreRegistrationListFragment newInstance() {

        Bundle args = new Bundle();

        StoreRegistrationListFragment fragment = new StoreRegistrationListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storeRegistrationViewModel = ViewModelProviders.of(this).get(StoreRegistrationViewModel.class);
    }

    public StoreRegistrationListFragment doOnStoreRegistrationRequestSelected(final StoreRegistrationRequestSelectListener listener) {
        this.listener = listener;
        return this;
    }

    public StoreRegistrationListFragment doOnNewRequestClicked(Runnable runnable) {
        newRequestAction = runnable;
        return this;
    }

    public StoreRegistrationListFragment onStoreRegistrationComplete() {
        storeRegistrationViewModel.getCombinedStoreRegistrationRequest().observe(this, storeRegistrationRequestAdapter::setStoreRegistrationRequestList);
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_registration_list, container, false);
        ButterKnife.bind(this, view);

        inflateHeader();
        inflateRecyclerView();
        return view;
    }

    private void inflateHeader() {
        requestId.setText(R.string.store_registration_request_id);
        storeName.setText(R.string.store_registration_request_name);
        result.setText(R.string.store_registration_request_state);
        createdDate.setText(R.string.store_registration_request_requested_date);
    }

    private void inflateRecyclerView() {
        storeRegistrationRequestAdapter = new StoreRegistrationRequestAdapter(listener);
        storeRegistrationViewModel.getCombinedStoreRegistrationRequest().observe(this, storeRegistrationRequestAdapter::setStoreRegistrationRequestList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(storeRegistrationRequestAdapter);
    }

    @OnClick(R.id.new_request)
    void onClickNewRequest() {
        newRequestAction.run();
    }

    public interface StoreRegistrationRequestSelectListener extends StoreRegistrationRequestAdapter.StoreRegistrationRequestSelectListener {
    }
}
