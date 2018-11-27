/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.menu.selector;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.store.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 11. 27.
 */
class MenuSelectorMenuAdapter extends RecyclerView.Adapter<MenuSelectorMenuAdapter.ViewHolder> {

    private final List<String> menuUuidListToExcept;
    private final List<String> menuUuidListToCheck;
    private final OnMenuCheckedListener listener;

    private final HashSet<CheckBox> checkBoxSet;

    private List<Menu> menuList;

    MenuSelectorMenuAdapter(final List<String> menuUuidListToExcept,
                            final List<String> menuUuidListToCheck,
                            final OnMenuCheckedListener listener) {

        this.menuUuidListToExcept = menuUuidListToExcept;
        this.menuUuidListToCheck = menuUuidListToCheck;
        this.listener = listener;

        menuList = new ArrayList<>();
        checkBoxSet = new HashSet<>();
    }

    void setMenuList(final List<Menu> menuList) {
        this.menuList.clear();
        this.menuList.addAll(menuList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_menu_setting_selector_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Menu menu = menuList.get(position);
        holder.name.setText(menu.name);

        holder.itemView.setOnClickListener(v -> holder.checkBox.performClick());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onMenuChecked(menu, isChecked));
        checkBoxSet.add(holder.checkBox);
    }

    void onClickParent(boolean isChecked) {
        Iterator<CheckBox> iterator = checkBoxSet.iterator();
        if (iterator == null) {
            return;
        }

        while (iterator.hasNext()) {
            CheckBox checkBox = iterator.next();
            if (checkBox.isChecked() != isChecked) {
                checkBox.performClick();
            }
        }
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    interface OnMenuCheckedListener {
        void onMenuChecked(Menu menu, boolean isChecked);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.check_box) AppCompatCheckBox checkBox;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
