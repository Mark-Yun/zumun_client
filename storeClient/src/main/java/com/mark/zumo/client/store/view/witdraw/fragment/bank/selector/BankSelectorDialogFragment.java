/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.witdraw.fragment.bank.selector;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.witdraw.fragment.bank.Bank;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 19. 2. 3.
 */
public class BankSelectorDialogFragment extends DialogFragment {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private OnBankSelectListener onBankSelectListener;

    public static BankSelectorDialogFragment newInstance() {

        Bundle args = new Bundle();

        BankSelectorDialogFragment fragment = new BankSelectorDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public BankSelectorDialogFragment onBankSelect(OnBankSelectListener onBankSelectListener) {
        this.onBankSelectListener = onBankSelectListener;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_framgent_bank_selector, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        BankSelectorAdapter bankSelectorAdapter = new BankSelectorAdapter(this::onSelectBankInternal);
        recyclerView.setAdapter(bankSelectorAdapter);
        bankSelectorAdapter.setBankList(createBankList());
    }

    private List<Bank> createBankList() {
        List<Bank> bankList = new ArrayList<>();
        String[] bankCodeArray = getContext().getResources().getStringArray(R.array.bank_codes);
        String[] bankNameArray = getContext().getResources().getStringArray(R.array.bank_entries);

        for (int i = 0; i < bankCodeArray.length; i++) {
            String bankName = bankNameArray[i];
            String bankCode = bankCodeArray[i];

            bankList.add(new Bank(bankName, bankCode));
        }

        return bankList;
    }

    private void onSelectBankInternal(Bank bank) {
        onBankSelectListener.onBankSelect(bank.name, bank.code);
        dismiss();
    }

    @OnClick(R.id.cancel)
    public void onCancelClicked() {
        dismiss();
    }

    public interface OnBankSelectListener {
        void onBankSelect(String bankName, String bankCode);
    }
}
