/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.witdraw.fragment.bank.selector;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.witdraw.fragment.bank.Bank;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 19. 2. 3.
 */
class BankSelectorAdapter extends RecyclerView.Adapter<BankSelectorAdapter.ViewHolder> {

    final BankSelectListener bankSelectListener;
    private final List<Bank> bankList;

    BankSelectorAdapter(final BankSelectListener bankSelectListener) {
        this.bankSelectListener = bankSelectListener;
        bankList = new CopyOnWriteArrayList<>();
    }

    void setBankList(List<Bank> bankList) {
        this.bankList.clear();
        this.bankList.addAll(bankList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_bank_selector, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Bank bank = this.bankList.get(i);

        viewHolder.name.setText(bank.name);
        viewHolder.itemView.setOnClickListener(v -> bankSelectListener.onBankSelect(bank));
    }

    @Override
    public int getItemCount() {
        return this.bankList.size();
    }

    interface BankSelectListener {
        void onBankSelect(Bank bank);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
