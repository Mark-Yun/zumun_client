/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.category;

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

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.SettingModeSelectee;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 8. 12.
 */
public class MenuCategorySettingMenuListAdapter extends RecyclerView.Adapter<MenuCategorySettingMenuListAdapter.ViewHolder>
        implements ItemTouchHelperAdapter, SettingModeSelectee {

    private final SelectMenuListener listener;

    private SettingMode settingMode = SettingMode.NONE;
    private HashSet<Runnable> modeUpdateOperationPool;

    private List<Menu> menuList;

    MenuCategorySettingMenuListAdapter(final SelectMenuListener listener) {
        this.listener = listener;
        menuList = new ArrayList<>();
        modeUpdateOperationPool = new HashSet<>();
    }

    List<Menu> getMenuList() {
        return menuList;
    }

    void setMenuList(final List<Menu> menuList) {
        this.menuList = menuList;
        notifyDataSetChanged();
    }

    void onMenuDetailCreated(List<Menu> createdMenuList) {
        int startPosition = this.menuList.size();
        this.menuList.addAll(createdMenuList);
        notifyItemRangeInserted(startPosition, this.menuList.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_category_setting_menu_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Menu menu = menuList.get(position);
        holder.name.setText(menu.name);

        Runnable modeUpdateOperation = () -> {
            holder.reorder.setVisibility(settingMode.isReorderMode() ? View.VISIBLE : View.GONE);
            holder.checkBox.setVisibility(settingMode.isDeleteMode() ? View.VISIBLE : View.GONE);
        };

        modeUpdateOperation.run();
        modeUpdateOperationPool.add(modeUpdateOperation);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {

    }

    @Override
    public void onItemDismiss(final int position) {

    }

    @Override
    public void onDrop() {

    }

    @Override
    public SettingMode getMode() {
        return settingMode;
    }

    @Override
    public void setMode(final SettingMode mode) {
        this.settingMode = mode;

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

    interface SelectMenuListener {
        void onModifyMenuList(List<Menu> menuList);
        void onDeleteMenuList(List<Menu> menuList);
        void onSelectMenuList(List<Menu> menuList);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.reorder) AppCompatImageView reorder;
        @BindView(R.id.check_box) AppCompatCheckBox checkBox;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
