/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.option;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.view.util.RecyclerUtils;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.SettingModeSelectee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 11. 11.
 */
class MenuOptionAdapter extends RecyclerView.Adapter<MenuOptionAdapter.ViewHolder>
        implements SettingModeSelectee {

    private static final String TAG = "MenuOptionAdapter";

    private List<String> optionNameList;
    private Map<String, List<MenuOption>> menuOptionMap;
    private MenuOptionSelectListener listener;

    private SettingMode settingMode = SettingMode.NONE;
    private HashSet<Runnable> modeUpdateOperationPool;

    MenuOptionAdapter() {
        optionNameList = new ArrayList<>();
        menuOptionMap = new HashMap<>();
        modeUpdateOperationPool = new HashSet<>();
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

    void setListener(final MenuOptionSelectListener listener) {
        this.listener = listener;
    }

    void setMenuOptionList(final List<MenuOption> menuOptionList) {
        Log.d(TAG, "setMenuOptionList: menuOptionList-" + menuOptionList.size());
        optionNameList.clear();
        menuOptionMap.clear();

        String name = "";
        for (MenuOption menuOption : menuOptionList) {
            if (!name.equals(menuOption.name)) {
                name = String.valueOf(menuOption.name);
                optionNameList.add(name);
            }

            if (!menuOptionMap.containsKey(name)) {
                menuOptionMap.put(name, new ArrayList<>());
            }

            List<MenuOption> menuOptions = menuOptionMap.get(name);
            menuOptions.add(menuOption);
            menuOptionMap.put(name, menuOptions);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_menu_option_setting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();

        String name = optionNameList.get(position);
        holder.name.setText(name);

        RecyclerView recyclerView = holder.optionRecyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        List<MenuOption> menuOptions = menuOptionMap.get(name);
        final MenuOptionDetailAdapter menuOptionDetailAdapter = new MenuOptionDetailAdapter(getMenuOptionSelectListener());

        recyclerView.setAdapter(menuOptionDetailAdapter);
        menuOptionDetailAdapter.setMenuOptionList(menuOptions);

        holder.menuOptionHeader.setOnClickListener(v -> onClickItemView(holder, name));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> menuOptionDetailAdapter.onClickParent(isChecked));

        View.OnClickListener recyclerViewExpandClickListener = RecyclerUtils.recyclerViewExpandButton(recyclerView, holder.expandButton);
        holder.expandClickArea.setOnClickListener(recyclerViewExpandClickListener);

        Runnable modeUpdateOperation = () -> {
            holder.reorder.setVisibility(settingMode.isReorderMode() ? View.VISIBLE : View.GONE);
            holder.checkBox.setVisibility(settingMode.isDeleteMode() ? View.VISIBLE : View.GONE);
            menuOptionDetailAdapter.setMode(settingMode);
        };
        modeUpdateOperation.run();
        modeUpdateOperationPool.add(modeUpdateOperation);
    }

    private void onClickItemView(final ViewHolder viewHolder, final String name) {
        if (settingMode.isEditMode()) {
            listener.onModifyMenuOption(menuOptionMap.get(name));
        } else if (settingMode.isDeleteMode()) {
            viewHolder.checkBox.performClick();
        } else {
            listener.onClickMenuOption(menuOptionMap.get(name));
        }
    }

    @NonNull
    private MenuOptionDetailAdapter.MenuOptionSelectListener getMenuOptionSelectListener() {
        return new MenuOptionDetailAdapter.MenuOptionSelectListener() {
            @Override
            public void onClickMenuOption(final MenuOption menuOption) {
                listener.onClickMenuOption(menuOption);
            }

            @Override
            public void onModifyMenuOption(final MenuOption menuOption) {
                listener.onModifyMenuOption(menuOption);
            }

            @Override
            public void onSelectMenuOption(final MenuOption menuOption, final boolean isChecked) {
                listener.onSelectMenuOption(menuOption, isChecked);
            }

            @Override
            public void onReorderMenuOption(final List<MenuOption> menuOptionList) {
                listener.onReorderMenuOption(menuOptionList);
            }
        };
    }

    @Override
    public int getItemCount() {
        return optionNameList.size();
    }

    interface MenuOptionSelectListener extends MenuOptionDetailAdapter.MenuOptionSelectListener {
        void onClickMenuOption(List<MenuOption> menuOptionList);
        void onModifyMenuOption(List<MenuOption> menuOptionList);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.expand_button) AppCompatImageView expandButton;
        @BindView(R.id.reorder) AppCompatImageView reorder;
        @BindView(R.id.menu_option_header) ConstraintLayout menuOptionHeader;
        @BindView(R.id.option_recycler_view) RecyclerView optionRecyclerView;
        @BindView(R.id.check_box) AppCompatCheckBox checkBox;
        @BindView(R.id.expand_click_area) ConstraintLayout expandClickArea;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
