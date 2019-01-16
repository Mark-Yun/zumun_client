/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.witdraw.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.user.store.StoreOwner;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.WithdrawViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 19. 1. 13.
 */
public class BankAccountFragment extends Fragment {

    @BindView(R.id.bank) AppCompatSpinner bank;
    @BindView(R.id.bank_account_text) TextInputEditText bankAccountText;

    private WithdrawViewModel withdrawViewModel;

    public static BankAccountFragment newInstance() {

        Bundle args = new Bundle();

        BankAccountFragment fragment = new BankAccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        withdrawViewModel = ViewModelProviders.of(this).get(WithdrawViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_account, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    private void inflateView() {
        withdrawViewModel.getSessionStoreOwner().observe(this, this::onLoadStoreUser);
    }

    private void onLoadStoreUser(StoreOwner storeOwner) {
        bankAccountText.setText(storeOwner.bankAccountNumber);
    }

    @OnClick(R.id.save)
    public void onViewClicked() {
    }
}
