/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.order.detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.database.entity.MenuOption;
import com.mark.zumo.client.customer.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 13.
 */
public class MenuOptionAdapter extends RecyclerView.Adapter<MenuOptionAdapter.ViewHolder> {

    private List<MenuOption> menuOptionList;

    MenuOptionAdapter() {
        menuOptionList = new ArrayList<>();
    }

    void setMenuOptionList(final List<MenuOption> menuOptionList) {
        this.menuOptionList = menuOptionList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_order_detail_menu_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MenuOption menuOption = menuOptionList.get(position);
        holder.name.setText(menuOption.name);
        holder.value.setText(menuOption.value);
    }

    @Override
    public int getItemCount() {
        return menuOptionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.value) AppCompatTextView value;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
