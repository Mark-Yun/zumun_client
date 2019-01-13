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
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.user.store.StoreOwner;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.WithdrawViewModel;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 19. 1. 13.
 */
public class WithdrawFragment extends Fragment {

    @BindView(R.id.available_money_icon) AppCompatImageView availableMoneyIcon;
    @BindView(R.id.available_money_text) AppCompatTextView availableMoneyText;
    @BindView(R.id.available_money_layout) ConstraintLayout availableMoneyLayout;
    @BindView(R.id.withdraw_icon) AppCompatImageView withdrawIcon;
    @BindView(R.id.withdraw_money_layout) ConstraintLayout withdrawMoneyLayout;
    @BindView(R.id.withdraw_text) TextInputEditText withdrawText;
    @BindView(R.id.withdraw_description) AppCompatTextView withdrawDescription;

    private WithdrawViewModel withdrawViewModel;

    public static WithdrawFragment newInstance() {

        Bundle args = new Bundle();

        WithdrawFragment fragment = new WithdrawFragment();
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
        View view = inflater.inflate(R.layout.fragment_withdraw, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
        setWithdrawCurrency(0);
        withdrawText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                setWithdrawCurrency(Integer.parseInt(s.toString()));
            }
        });
        withdrawViewModel.getSessionStoreOwner().observe(this, this::onLoadStoreUser);
    }

    private void onLoadStoreUser(StoreOwner storeOwner) {
        String withDrawMoneyDescriptionText = getString(R.string.withdraw_money_description, storeOwner.bankCode, storeOwner.bankAccountNumber);
        withdrawDescription.setText(withDrawMoneyDescriptionText);
    }

    private void setWithdrawCurrency(int amount) {
        String emptyCurrency = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount);
        withdrawText.setText(emptyCurrency);
    }

    @OnClick(R.id.withdraw)
    public void onViewClicked() {
    }
}
