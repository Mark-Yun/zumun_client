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

package com.mark.zumo.client.store.view.witdraw.fragment.bank;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mark.zumo.client.core.entity.user.store.StoreOwner;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.witdraw.fragment.bank.selector.BankSelectorDialogFragment;
import com.mark.zumo.client.store.viewmodel.BankViewModel;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 19. 1. 13.
 */
public class BankAccountFragment extends Fragment {

    @BindView(R.id.bank) AppCompatTextView bank;
    @BindView(R.id.bank_account_text) TextInputEditText bankAccountText;
    @BindView(R.id.bank_account_owner_birth) TextInputEditText bankAccountOwnerBirth;
    @BindView(R.id.bank_account_owner_sex) TextInputEditText bankAccountOwnerSex;
    @BindView(R.id.id_number) AppCompatTextView idNumber;

    private BankViewModel bankViewModel;
    private Map<String, String> bankCodeMap;

    private String selectedBankCode;

    public static BankAccountFragment newInstance() {

        Bundle args = new Bundle();

        BankAccountFragment fragment = new BankAccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bankViewModel = ViewModelProviders.of(this).get(BankViewModel.class);
        bankCodeMap = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_account, container, false);
        ButterKnife.bind(this, view);
        inflateView();

        return view;
    }

    private void inflateView() {
        bankViewModel.getSessionStoreOwner().observe(this, this::onLoadStoreUser);
        bankCodeMap.putAll(createBankCodeMap());
    }

    private Map<String, String> createBankCodeMap() {
        Map<String, String> bankMap = new HashMap<>();
        Context context = getContext();
        if (context == null) {
            return bankMap;
        }

        Resources resources = context.getResources();

        String[] bankCodeArray = resources.getStringArray(R.array.bank_codes);
        String[] bankNameArray = resources.getStringArray(R.array.bank_entries);

        for (int i = 0; i < bankCodeArray.length; i++) {
            String bankName = bankNameArray[i];
            String bankCode = bankCodeArray[i];

            bankMap.put(bankCode, bankName);
        }

        return bankMap;
    }

    private void onLoadStoreUser(StoreOwner storeOwner) {
        bankAccountText.setText(storeOwner.bankAccountNumber);

        if (TextUtils.isEmpty(storeOwner.bankCode) && bankCodeMap.containsKey(storeOwner.bankCode)) {
            this.selectedBankCode = storeOwner.bankCode;
            bank.setText(bankCodeMap.get(storeOwner.bankCode));
        }
    }

    @OnClick(R.id.save)
    public void onSaveClicked() {
        String birth = bankAccountOwnerBirth.getText() == null ? "" : bankAccountOwnerBirth.getText().toString();
        String sex = bankAccountOwnerSex.getText() == null ? "" : bankAccountOwnerSex.getText().toString();
        String bankAccountNumber = bankAccountText.getText() == null ? "" : bankAccountText.getText().toString();

        bankViewModel.inquiryBankAccount(birth, sex, selectedBankCode, bankAccountNumber)
                .observe(this, success -> onInquiryBankAccount(selectedBankCode, bankAccountNumber, success));

    }

    private void onInquiryBankAccount(String bankCode, String bankAccountNumber, boolean success) {
        if (!success) {
            Toast.makeText(getActivity(), "Wrong bank account", Toast.LENGTH_SHORT).show();
            return;
        }

        bankViewModel.updateBankAccount(bankCode, bankAccountNumber)
                .observe(this, this::onUpdatedBankInfo);
    }

    private void onUpdatedBankInfo(StoreOwner storeOwner) {
        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.bank)
    public void onBankClicked() {
        BankSelectorDialogFragment.newInstance()
                .onBankSelect(this::onSelectBank)
                .show(getFragmentManager(), this.getClass().getName());
    }

    private void onSelectBank(String bankName, String bankCode) {
        this.selectedBankCode = bankCode;
        bank.setText(bankName);
    }
}
