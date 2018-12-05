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

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.option.optionlist;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.SettingModeSelectee;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 11. 11.
 */
class MenuOptionSettingOptionDetailListAdapter extends RecyclerView.Adapter<MenuOptionSettingOptionDetailListAdapter.ViewHolder>
        implements SettingModeSelectee {

    private final MenuOptionSelectListener menuOptionSelectListener;
    final private List<Runnable> modeUpdateOperationPool;
    final private List<CheckBox> checkBoxSet;
    private SettingMode settingMode = SettingMode.NONE;
    private List<MenuOption> menuOptionList;

    MenuOptionSettingOptionDetailListAdapter(@NonNull MenuOptionSelectListener menuOptionSelectListener) {
        this.menuOptionSelectListener = menuOptionSelectListener;

        menuOptionList = new ArrayList<>();
        modeUpdateOperationPool = new CopyOnWriteArrayList<>();
        checkBoxSet = new CopyOnWriteArrayList<>();
    }

    void setMenuOptionList(final List<MenuOption> menuOptionList) {
        if (menuOptionList == null || menuOptionList.isEmpty()) {
            return;
        }
        this.menuOptionList.clear();
        this.menuOptionList.addAll(menuOptionList);
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
        holder.price.setText(NumberFormat.getCurrencyInstance().format(menuOption.price));

        holder.itemView.setOnClickListener(v -> onClickItemView(holder, menuOption));
        Runnable runnable = () -> {
            holder.checkBox.setVisibility(settingMode.isDeleteMode() ? View.VISIBLE : View.GONE);
            holder.reorder.setVisibility(settingMode.isReorderMode() ? View.VISIBLE : View.GONE);
        };
        runnable.run();
        modeUpdateOperationPool.add(runnable);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> menuOptionSelectListener.onSelectMenuOption(menuOption, isChecked));
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

    private void onClickItemView(final ViewHolder holder, final MenuOption menuOption) {
        if (settingMode.isEditMode()) {
            menuOptionSelectListener.onModifyMenuOption(menuOption);
        } else if (settingMode.isDeleteMode()) {
            holder.checkBox.performClick();
            menuOptionSelectListener.onSelectMenuOption(menuOption, holder.checkBox.isChecked());
        } else {
            menuOptionSelectListener.onClickMenuOption(menuOption);
        }
    }

    @Override
    public int getItemCount() {
        return menuOptionList.size();
    }

    @Override
    public SettingMode getMode() {
        return settingMode;
    }

    @Override
    public void setMode(final SettingMode mode) {
        settingMode = mode;
        notifyModeChange();
    }

    private void notifyModeChange() {
        Iterator<Runnable> iterator = modeUpdateOperationPool.iterator();
        if (iterator == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            while (iterator.hasNext()) {
                iterator.next().run();
            }
        });
    }

    void onMenuOptionListRemoved(List<MenuOption> removedMenuOptionList) {

    }

    void onMenuOptionListUpdated(List<MenuOption> updatedMenuOptionList) {

    }

    interface MenuOptionSelectListener {
        void onClickMenuOption(MenuOption menuOption);
        void onModifyMenuOption(MenuOption menuOption);
        void onSelectMenuOption(MenuOption menuOption, boolean isChecked);
        void onReorderMenuOption(List<MenuOption> menuOptionList);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.value) AppCompatTextView value;
        @BindView(R.id.price) AppCompatTextView price;
        @BindView(R.id.check_box) AppCompatCheckBox checkBox;
        @BindView(R.id.reorder) AppCompatImageView reorder;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
