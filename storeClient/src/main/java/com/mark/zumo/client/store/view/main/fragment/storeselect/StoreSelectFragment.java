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

package com.mark.zumo.client.store.view.main.fragment.storeselect;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MainViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 12. 27.
 */
public class StoreSelectFragment extends Fragment {

    private static final String TAG = "StoreSelectFragment";

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.back) AppCompatImageView back;

    private MainViewModel mainViewModel;

    private StoreSelectListener listener;
    private Runnable storeRegistrationAction;

    public static StoreSelectFragment newInstance() {

        Bundle args = new Bundle();

        StoreSelectFragment fragment = new StoreSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public StoreSelectFragment onSelectStore(final StoreSelectListener listener) {
        this.listener = listener;
        return this;
    }

    public StoreSelectFragment onClickStoreRegistration(Runnable storeRegistrationAction) {
        this.storeRegistrationAction = storeRegistrationAction;
        return this;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_select, container, false);
        ButterKnife.bind(this, view);

        inflateView();

        return view;
    }

    private void inflateView() {
        StoreSelectAdapter storeSelectAdapter = new StoreSelectAdapter(this::onSelectStore);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(storeSelectAdapter);
        mainViewModel.getStoreUserContractedStoreList().observe(this, storeSelectAdapter::setStoreList);

        boolean hasBackStack = getFragmentManager().getBackStackEntryCount() > 0;
        back.setVisibility(hasBackStack ? View.VISIBLE : View.GONE);
    }

    private void onSelectStore(Store store) {
        if (TextUtils.isEmpty(store.uuid)) {
            storeRegistrationAction.run();
        } else {
            mainViewModel.setSessionStore(store.uuid).observe(this, listener::onSelectStore);
        }
    }

    @OnClick(R.id.back)
    void onBackClicked() {
        mainViewModel.signOut();
        getFragmentManager().popBackStack();
    }

    public interface StoreSelectListener extends StoreSelectAdapter.StoreSelectListener {
    }
}
