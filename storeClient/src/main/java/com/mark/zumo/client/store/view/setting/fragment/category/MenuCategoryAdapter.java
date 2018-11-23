/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.category;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleOwner;
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

import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 27.
 */
class MenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryAdapter.ViewHolder>
        implements ItemTouchHelperAdapter, MenuCategorySettingModeSelectee {

    private final OnSelectCategoryListener onSelectCategoryListener;
    private final MenuSettingViewModel menuSettingViewModel;
    private final LifecycleOwner lifecycleOwner;
    private final OnStartDragListener dragStartListener;

    private List<MenuCategory> menuCategoryList;

    private MenuCategorySettingModeSelectee.SettingMode settingMode = MenuCategorySettingModeSelectee.SettingMode.NONE;
    private HashSet<Runnable> modeUpdateOperationPool;

    MenuCategoryAdapter(final OnSelectCategoryListener onSelectCategoryListener,
                        final MenuSettingViewModel menuSettingViewModel,
                        final LifecycleOwner lifecycleOwner,
                        final OnStartDragListener dragStartListener) {

        this.onSelectCategoryListener = onSelectCategoryListener;
        this.menuSettingViewModel = menuSettingViewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.dragStartListener = dragStartListener;

        menuCategoryList = new ArrayList<>();
        modeUpdateOperationPool = new HashSet<>();
    }

    void setMenuCategoryList(final List<MenuCategory> menuCategoryList) {
        this.menuCategoryList = menuCategoryList;
        notifyDataSetChanged();
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_menu_category_setting, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final MenuCategory menuCategory = menuCategoryList.get(position);

        holder.reorder.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    dragStartListener.onStartDrag(holder);
                    break;
            }
            return false;
        });

        holder.name.setText(menuCategory.name);
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> onSelectCategoryListener.onSelectMenuCategory(menuCategory, isChecked));
        holder.itemView.setOnClickListener(v -> onClickItemView(holder, menuCategory));

        Runnable modeUpdateOperation = () -> {
            holder.reorder.setVisibility(settingMode.isReorderMode() ? View.VISIBLE : View.GONE);
            holder.checkBox.setVisibility(settingMode.isDeleteMode() ? View.VISIBLE : View.GONE);
        };

        modeUpdateOperation.run();
        modeUpdateOperationPool.add(modeUpdateOperation);
    }

    private void onClickItemView(final ViewHolder viewHolder, final MenuCategory menuCategory) {
        if (settingMode.isEditMode()) {
            onSelectCategoryListener.onModifyMenuCategory(menuCategory, updatedMenuCategory ->
                    viewHolder.name.setText(updatedMenuCategory.name)
            );
        } else if (settingMode.isDeleteMode()) {
            viewHolder.checkBox.performClick();
        } else {
            onSelectCategoryListener.onClickMenuCategoryOption(menuCategory);
        }
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {
        Collections.swap(menuCategoryList, fromPosition - 1, toPosition - 1);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(final int position) {
        //Empty Body
    }

    void onCreateMenuCategory(MenuCategory menuCategory) {
        menuCategoryList.add(menuCategoryList.size() - 1, menuCategory);
        notifyItemInserted(menuCategoryList.size() - 2);
    }

    void onRemoveCategory(final List<MenuCategory> menuCategoryList) {
        for (MenuCategory removedMenuCategory : menuCategoryList) {
            List<MenuCategory> tmpCategoryList = new ArrayList<>(this.menuCategoryList);
            for (int i = 0; i < tmpCategoryList.size(); i++) {
                if (this.menuCategoryList.get(i).uuid.equals(removedMenuCategory.uuid)) {
                    this.menuCategoryList.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onDrop() {
        saveCategoryListSeq();
    }

    private void saveCategoryListSeq() {
        for (MenuCategory menuCategory : menuCategoryList) {
            menuCategory.seqNum = menuCategoryList.indexOf(menuCategory);
        }

        menuSettingViewModel.updateCategorySeqNum(menuCategoryList).observe(lifecycleOwner, this::onLoadMenuCategoryList);
    }

    private void onLoadMenuCategoryList(List<MenuCategory> menuCategoryList) {
        this.menuCategoryList = menuCategoryList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return menuCategoryList.size();
    }

    interface OnSelectCategoryListener {
        void onSelectMenuCategory(MenuCategory menuCategory);
        void onClickMenuCategoryOption(MenuCategory menuCategory);
        void onModifyMenuCategory(MenuCategory menuCategory, MenuCategoryUpdateListener listener);
        void onSelectMenuCategory(MenuCategory menuCategory, boolean isChecked);
        void onReorderMenuCategory(List<MenuCategory> menuCategoryList);

        @FunctionalInterface
        interface MenuCategoryUpdateListener {
            void onMenuCategoryUpdated(MenuCategory menuCategory);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.reorder) AppCompatImageView reorder;
        @BindView(R.id.check_box) AppCompatCheckBox checkBox;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
