/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.category;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
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
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 27.
 */
class MenuCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private final OnSelectCategoryListener onSelectCategoryListener;
    private final MenuSettingViewModel menuSettingViewModel;
    private final LifecycleOwner lifecycleOwner;
    private final OnStartDragListener dragStartListener;

    private List<MenuCategory> menuCategoryList;

    MenuCategoryAdapter(final OnSelectCategoryListener onSelectCategoryListener,
                        final MenuSettingViewModel menuSettingViewModel,
                        final LifecycleOwner lifecycleOwner,
                        final OnStartDragListener dragStartListener) {

        this.onSelectCategoryListener = onSelectCategoryListener;
        this.menuSettingViewModel = menuSettingViewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.dragStartListener = dragStartListener;

        menuCategoryList = new ArrayList<>();
    }

    void setMenuCategoryList(final List<MenuCategory> menuCategoryList) {
        this.menuCategoryList = menuCategoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            View view = layoutInflater.inflate(R.layout.card_view_menu_category_setting_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.card_view_menu_category_setting, parent, false);
            return new BodyViewHolder(view);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BodyViewHolder) {
            final BodyViewHolder viewHolder = (BodyViewHolder) holder;
            final Context context = viewHolder.itemView.getContext();
            final MenuCategory menuCategory = menuCategoryList.get(position - 1);

            viewHolder.reorder.setOnTouchListener((v, event) -> {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        dragStartListener.onStartDrag(holder);
                        break;
                }
                return false;
            });

            viewHolder.name.setText(menuCategory.name);
            viewHolder.name.setOnClickListener(v -> {
                final AppCompatEditText editText = new AppCompatEditText(context);
                new AlertDialog.Builder(context)
                        .setTitle(R.string.menu_category_setting_update_category_dialog_title)
                        .setMessage(R.string.menu_category_setting_update_category_dialog_message)
                        .setView(editText)
                        .setCancelable(true)
                        .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                        .setPositiveButton(R.string.button_text_apply, (dialog, which) -> {
                            menuSettingViewModel.updateCategoryName(menuCategory, editText.getText().toString())
                                    .observe(lifecycleOwner, updatedMenuCategory -> viewHolder.name.setText(Objects.requireNonNull(updatedMenuCategory).name));
                            dialog.dismiss();
                        })
                        .create()
                        .show();
            });

            viewHolder.removeButton.setOnClickListener(v ->
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.menu_category_setting_remove_category_dialog_title)
                            .setMessage(context.getString(R.string.menu_category_setting_remove_category_dialog_message, menuCategory.name))
                            .setCancelable(true)
                            .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                menuSettingViewModel.removeCategory(menuCategory)
                                        .observe(lifecycleOwner, x -> {
                                            int removedPosition = menuCategoryList.indexOf(menuCategory);
                                            menuCategoryList.remove(menuCategory);
                                            notifyItemRemoved(removedPosition + 1);
                                        });
                                dialog.dismiss();
                            })
                            .create()
                            .show()
            );

            viewHolder.itemView.setOnClickListener(v -> onSelectCategory(menuCategory));
        } else if (holder instanceof HeaderViewHolder) {
            final HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            final Context context = viewHolder.itemView.getContext();

            viewHolder.itemView.setOnClickListener(v -> {
                AppCompatEditText editText = new AppCompatEditText(context);

                new AlertDialog.Builder(context)
                        .setTitle(R.string.menu_category_setting_add_new_category_dialog_title)
                        .setMessage(R.string.menu_category_setting_add_new_category_dialog_message)
                        .setView(editText)
                        .setCancelable(true)
                        .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                        .setPositiveButton(R.string.button_text_apply, (dialog, which) -> {
                            int seqNum = -1;
                            for (MenuCategory menuCategory : menuCategoryList) {
                                seqNum = Math.max(seqNum, menuCategory.seqNum);
                            }
                            menuSettingViewModel.createCategory(editText.getText().toString(), seqNum + 1)
                                    .observe(lifecycleOwner, this::onCreateMenuCategory);
                            dialog.dismiss();
                        })
                        .create()
                        .show();
            });
        }
    }

    private void onSelectCategory(MenuCategory menuCategory) {
        OnSelectCategoryListener onSelectCategoryListener = this.onSelectCategoryListener;
        if (onSelectCategoryListener == null) {
            return;
        }

        onSelectCategoryListener.onSelectCategory(menuCategory);
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

    private void onCreateMenuCategory(MenuCategory menuCategory) {
        menuCategoryList.add(menuCategory);
        notifyItemInserted(menuCategoryList.size());
    }

    @Override
    public void onDrop() {
        saveCategoryListSeq();
    }

    @Override
    public int getItemViewType(final int position) {
        return position == 0 ? 0 : 1;
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
        return menuCategoryList.size() + 1;
    }

    interface OnSelectCategoryListener {
        void onSelectCategory(MenuCategory menuCategory);
    }

    static class BodyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.reorder) AppCompatImageView reorder;
        @BindView(R.id.remove_button) AppCompatImageButton removeButton;

        private BodyViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private HeaderViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
