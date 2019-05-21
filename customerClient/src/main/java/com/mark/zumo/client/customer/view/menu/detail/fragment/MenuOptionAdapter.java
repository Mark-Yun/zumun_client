/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu.detail.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mark.zumo.client.core.database.entity.MenuOption;
import com.mark.zumo.client.core.database.entity.MenuOptionCategory;
import com.mark.zumo.client.customer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.exceptions.OnErrorNotImplementedException;

/**
 * Created by mark on 18. 5. 24.
 */
class MenuOptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MenuOptionAdapter";

    private static final int VIEW_TYPE_SINGLE_OPTION = 0;
    private static final int VIEW_TYPE_MULTI_OPTION = 1;

    private final MenuOptionSelectListener menuOptionSelectListener;
    private final List<MenuOptionCategory> menuOptionCategoryList;
    private final List<String> selectedMenuOptionUuidList;

    MenuOptionAdapter(MenuOptionSelectListener menuOptionSelectListener) {
        this.menuOptionSelectListener = menuOptionSelectListener;

        menuOptionCategoryList = new CopyOnWriteArrayList<>();
        selectedMenuOptionUuidList = new CopyOnWriteArrayList<>();
    }

    void setMenuOptionCategoryList(final List<MenuOptionCategory> menuOptionCategoryList) {
        this.menuOptionCategoryList.clear();
        this.menuOptionCategoryList.addAll(menuOptionCategoryList);
        notifyDataSetChanged();
    }

    void setSelectedMenuOptionUuidList(final List<String> selectedMenuOptionUuidList) {
        this.selectedMenuOptionUuidList.clear();
        this.selectedMenuOptionUuidList.addAll(selectedMenuOptionUuidList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        int resId;
        switch (viewType) {
            case VIEW_TYPE_SINGLE_OPTION:
                resId = R.layout.card_view_menu_option_single_select;
                View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                return new SingleSelectViewHolder(view);

            case VIEW_TYPE_MULTI_OPTION:
                resId = R.layout.card_view_menu_option_multi_select;
                view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                return new MultiSelectViewHolder(view);

            default:
                throw new OnErrorNotImplementedException(new Throwable());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        MenuOptionCategory menuOptionCategory = menuOptionCategoryList.get(position);
        List<MenuOption> menuOptionList = menuOptionCategory.menuOptionList;

        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case VIEW_TYPE_SINGLE_OPTION:
                SingleSelectViewHolder singleSelectViewHolder = (SingleSelectViewHolder) holder;
                singleSelectViewHolder.name.setText(menuOptionCategory.name);

                boolean isSelected = selectedMenuOptionUuidList.contains(menuOptionList.get(0).uuid);
                singleSelectViewHolder.checkBox.setChecked(isSelected);

                singleSelectViewHolder.itemView.setOnClickListener(v -> singleSelectViewHolder.checkBox.performClick());
                singleSelectViewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                        menuOptionSelectListener.onSingleMenuOptionSelected(menuOptionList.get(0), isChecked)
                );
                break;

            case VIEW_TYPE_MULTI_OPTION:
                MultiSelectViewHolder multiSelectViewHolder = (MultiSelectViewHolder) holder;

                multiSelectViewHolder.name.setText(menuOptionCategory.name);

                List<String> menuStringList = new ArrayList<>();
                for (MenuOption option : menuOptionList) {

                    isSelected = selectedMenuOptionUuidList.contains(menuOptionList.get(0).uuid);
                    if (isSelected) {
                        multiSelectViewHolder.value.setText(option.value);
                    }

                    String text = option.value;
                    if (option.price > 0) {
                        text += "     " + String.valueOf(option.price);
                    }
                    menuStringList.add(text);
                }

                Context context = multiSelectViewHolder.itemView.getContext();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item, menuStringList);

                multiSelectViewHolder.itemView.setOnClickListener(v ->
                        new AlertDialog.Builder(context)
                                .setTitle(menuOptionCategory.name)
                                .setAdapter(arrayAdapter, (dialog, which) -> {
                                    MenuOption menuOption = menuOptionList.get(which);
                                    multiSelectViewHolder.value.setText(menuOption.value);
                                    menuOptionSelectListener.onMultiMenuOptionSelected(menuOption);
                                })
                                .setCancelable(true)
                                .setOnCancelListener(dialog -> menuOptionSelectListener.onMenuOptionCategoryCanceled(menuOptionCategory))
                                .create().show()
                );
                break;
        }
    }

    @Override
    public int getItemCount() {
        return menuOptionCategoryList.size();
    }

    @Override
    public int getItemViewType(final int position) {
        boolean isSingleOption = menuOptionCategoryList.get(position).menuOptionList.size() == 1;

        return isSingleOption
                ? VIEW_TYPE_SINGLE_OPTION
                : VIEW_TYPE_MULTI_OPTION;
    }

    interface MenuOptionSelectListener {
        void onSingleMenuOptionSelected(MenuOption menuOption, boolean isChecked);
        void onMultiMenuOptionSelected(MenuOption menuOption);
        void onMenuOptionCategoryCanceled(MenuOptionCategory menuOptionCategory);
    }
}
