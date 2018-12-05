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

package com.mark.zumo.client.store.view.setting.fragment.option.menulist;

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

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.SettingModeSelectee;
import com.mark.zumo.client.store.view.util.draghelper.reorder.ItemTouchHelperAdapter;
import com.mark.zumo.client.store.view.util.draghelper.reorder.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 8. 12.
 */
public class MenuOptionSettingMenuListAdapter extends RecyclerView.Adapter<MenuOptionSettingMenuListAdapter.ViewHolder>
        implements ItemTouchHelperAdapter, SettingModeSelectee {

    private final SelectMenuListener listener;
    private final OnStartDragListener dragStartListener;

    private SettingMode settingMode = SettingMode.NONE;
    private HashSet<Runnable> modeUpdateOperationPool;

    private List<Menu> menuList;

    MenuOptionSettingMenuListAdapter(final SelectMenuListener listener,
                                     final OnStartDragListener dragStartListener) {
        this.listener = listener;
        this.dragStartListener = dragStartListener;
        menuList = new ArrayList<>();
        modeUpdateOperationPool = new HashSet<>();
    }

    List<Menu> getMenuList() {
        return menuList;
    }

    void setMenuList(final List<Menu> menuList) {
        if (menuList == null || menuList.isEmpty()) {
            return;
        }

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
        View view = layoutInflater.inflate(R.layout.card_view_option_setting_menu_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Menu menu = menuList.get(position);
        holder.name.setText(menu.name);

        holder.reorder.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    dragStartListener.onStartDrag(holder);
                    break;
            }
            return false;
        });

        Runnable modeUpdateOperation = () -> {
            holder.reorder.setVisibility(settingMode.isReorderMode() ? View.VISIBLE : View.GONE);
            holder.checkBox.setVisibility(settingMode.isDeleteMode() ? View.VISIBLE : View.GONE);
        };

        modeUpdateOperation.run();
        modeUpdateOperationPool.add(modeUpdateOperation);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onSelectMenuList(menu, isChecked));
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    void onRemoveMenuList(final List<Menu> removedMenuList) {
        for (Menu removedMenu : removedMenuList) {
            List<Menu> tmpMenuList = new ArrayList<>(this.menuList);
            for (int i = 0; i < tmpMenuList.size(); i++) {
                if (this.menuList.get(i).uuid.equals(removedMenu.uuid)) {
                    this.menuList.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {
        Collections.swap(menuList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(final int position) {

    }

    @Override
    public void onDrop() {
        listener.onReorderMenuList(menuList);
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
        void onReorderMenuList(List<Menu> menu);
        void onDeleteMenuList(List<Menu> menuList);
        void onSelectMenuList(Menu menu, boolean isChecked);
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
