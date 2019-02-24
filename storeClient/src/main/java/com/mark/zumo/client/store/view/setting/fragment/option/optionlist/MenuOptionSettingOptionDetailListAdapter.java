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

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.SettingModeSelectee;
import com.mark.zumo.client.store.view.util.draghelper.reorder.ItemTouchHelperAdapter;
import com.mark.zumo.client.store.view.util.draghelper.reorder.OnStartDragListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 11. 11.
 */
class MenuOptionSettingOptionDetailListAdapter extends RecyclerView.Adapter<MenuOptionSettingOptionDetailListAdapter.ViewHolder>
        implements SettingModeSelectee, ItemTouchHelperAdapter {

    private final MenuOptionSelectListener menuOptionSelectListener;
    private final List<Runnable> modeUpdateOperationPool;
    private final List<CheckBox> checkBoxSet;
    private final List<MenuOption> menuOptionList;

    private OnStartDragListener onStartDragListener;

    private SettingMode settingMode = SettingMode.NONE;

    MenuOptionSettingOptionDetailListAdapter(@NonNull final MenuOptionSelectListener menuOptionSelectListener) {
        this.menuOptionSelectListener = menuOptionSelectListener;

        menuOptionList = new ArrayList<>();
        modeUpdateOperationPool = new CopyOnWriteArrayList<>();
        checkBoxSet = new CopyOnWriteArrayList<>();
    }

    void setOnStartDragListener(final OnStartDragListener onStartDragListener) {
        this.onStartDragListener = onStartDragListener;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MenuOption menuOption = menuOptionList.get(position);
        holder.value.setText(menuOption.value);
        holder.price.setText(NumberFormat.getCurrencyInstance().format(menuOption.price));

        holder.itemView.setOnClickListener(v -> onClickItemView(holder, menuOption));

        holder.reorder.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    onStartDragListener.onStartDrag(holder);
                    break;
            }
            return false;
        });

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
        for (final CheckBox checkBox : checkBoxSet) {
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
        for (MenuOption removedMenuOption : removedMenuOptionList) {
            List<MenuOption> tmpMenuOptionList = new ArrayList<>(this.menuOptionList);
            for (int i = 0; i < tmpMenuOptionList.size(); i++) {
                if (this.menuOptionList.get(i).uuid.equals(removedMenuOption.uuid)) {
                    this.menuOptionList.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {
        Collections.swap(menuOptionList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(final int position) {
        //Empty body
    }

    @Override
    public void onDrop() {
        for (MenuOption menuOption : menuOptionList) {
            menuOption.seqNum = menuOptionList.indexOf(menuOption);
        }

        menuOptionSelectListener.onReorderMenuOption(menuOptionList);
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
