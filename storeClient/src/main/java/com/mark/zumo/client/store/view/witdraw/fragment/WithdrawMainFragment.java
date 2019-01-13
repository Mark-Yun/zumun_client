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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.WithdrawViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 19. 1. 13.
 */
public class WithdrawMainFragment extends Fragment {

    @BindView(R.id.withdraw_indicator) ConstraintLayout withdrawIndicator;
    @BindView(R.id.bank_account_indicator) ConstraintLayout bankAccountIndicator;
    @BindView(R.id.main_activity_background_image) AppCompatImageView mainActivityBackgroundImage;

    private WithdrawViewModel withdrawViewModel;

    public static WithdrawMainFragment newInstance() {

        Bundle args = new Bundle();

        WithdrawMainFragment fragment = new WithdrawMainFragment();
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
        View view = inflater.inflate(R.layout.fragment_withdraw_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void inflateView() {

    }

    private void inflateFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.withdraw_content_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @OnClick(R.id.withdraw_indicator)
    public void onWithdrawIndicatorClicked() {
        WithdrawFragment withdrawFragment = WithdrawFragment.newInstance();
        inflateFragment(withdrawFragment);
    }

    @OnClick(R.id.bank_account_indicator)
    public void onBankAccountIndicatorClicked() {
        BankAccountFragment bankAccountFragment = BankAccountFragment.newInstance();
        inflateFragment(bankAccountFragment);
    }
}
