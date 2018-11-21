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

package com.mark.zumo.client.store.view.setting.fragment.option;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.store.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 11. 11.
 */
class MenuOptionDetailAdapter extends RecyclerView.Adapter<MenuOptionDetailAdapter.ViewHolder> {

    private boolean isReorderMode;
    private boolean isDeleteMode;
    private boolean isEditMode;

    private List<MenuOption> menuOptionList;

    MenuOptionDetailAdapter() {
        menuOptionList = new ArrayList<>();
    }

    void setMenuOptionList(final List<MenuOption> menuOptionList) {
        this.menuOptionList = menuOptionList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_menu_option_setting_detail_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MenuOption menuOption = menuOptionList.get(position);
        holder.value.setText(menuOption.value);
        String priceText = "(" + NumberFormat.getCurrencyInstance().format(menuOption.price) + ")";
        holder.price.setText(priceText);
        holder.reorder.setVisibility(isReorderMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return menuOptionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.value) AppCompatTextView value;
        @BindView(R.id.price) AppCompatTextView price;
        @BindView(R.id.reorder) AppCompatImageView reorder;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
