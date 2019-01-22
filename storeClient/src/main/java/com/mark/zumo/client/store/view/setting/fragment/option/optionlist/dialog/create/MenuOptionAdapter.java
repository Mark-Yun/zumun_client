/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.option.optionlist.dialog.create;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.store.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

/**
 * Created by mark on 18. 12. 7.
 */
class MenuOptionAdapter extends RecyclerView.Adapter<MenuOptionAdapter.ViewHolder> {

    private final List<MenuOption.Builder> menuOptionBuilderList;

    MenuOptionAdapter() {
        menuOptionBuilderList = new ArrayList<>();
        menuOptionBuilderList.add(new MenuOption.Builder());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_dialog_fragment_menu_option_cateogry_create_option_value, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MenuOption.Builder menuOptionBuilder = menuOptionBuilderList.get(position);
        holder.remove.setOnClickListener(v -> onRemove(menuOptionBuilder));
        holder.value.setText("");
        holder.value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                if (TextUtils.isEmpty(s)) {
                    return;
                }

                menuOptionBuilder.setValue(String.valueOf(s));
                onChangedOptionInput();
            }

            @Override
            public void afterTextChanged(final Editable s) {
            }
        });


        holder.price.setText("");
        holder.price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                if (TextUtils.isEmpty(s)) {
                    return;
                }

                menuOptionBuilder.setPrice(Integer.parseInt(s.toString()));
                onChangedOptionInput();
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });
    }

    List<MenuOption> getMenuOptionList() {
        return Observable.fromIterable(menuOptionBuilderList)
                .map(MenuOption.Builder::build)
                .toList().blockingGet();
    }

    private void onChangedOptionInput() {
        for (MenuOption.Builder builder : menuOptionBuilderList) {
            if (TextUtils.isEmpty(builder.getValue())) {
                return;
            }

            if (builder.getPrice() < 0) {
                return;
            }
        }

        menuOptionBuilderList.add(new MenuOption.Builder());
        notifyItemInserted(menuOptionBuilderList.size() - 1);
    }

    private void onRemove(MenuOption.Builder builder) {
        int index = menuOptionBuilderList.indexOf(builder);
        if (index < 0 || index >= menuOptionBuilderList.size()) {
            return;
        }

        menuOptionBuilderList.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return menuOptionBuilderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.option_value) TextInputEditText value;
        @BindView(R.id.option_price) TextInputEditText price;
        @BindView(R.id.remove) AppCompatImageView remove;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
