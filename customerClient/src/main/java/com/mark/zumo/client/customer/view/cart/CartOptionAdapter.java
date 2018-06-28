/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.cart;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 28.
 */
public class CartOptionAdapter extends RecyclerView.Adapter<CartOptionAdapter.OptionViewHolder> {

    private final CartViewModel cartViewModel;
    private final LifecycleOwner lifecycleOwner;

    private List<String> menuOptionList;

    CartOptionAdapter(final CartViewModel cartViewModel, final LifecycleOwner lifecycleOwner) {
        this.cartViewModel = cartViewModel;
        this.lifecycleOwner = lifecycleOwner;

        menuOptionList = new ArrayList<>();
    }

    void setMenuOptionList(final List<String> menuOptionList) {
        this.menuOptionList = menuOptionList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_cart_item_menu_option, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OptionViewHolder holder, final int position) {
        String menuOptionUuid = menuOptionList.get(position);

        cartViewModel.getMenuOption(menuOptionUuid).observe(lifecycleOwner, menuOption -> {
            holder.name.setText(Objects.requireNonNull(menuOption).name);
            holder.value.setText(menuOption.value);
        });
    }

    @Override
    public int getItemCount() {
        return menuOptionList.size();
    }

    class OptionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.value) AppCompatTextView value;

        private OptionViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
