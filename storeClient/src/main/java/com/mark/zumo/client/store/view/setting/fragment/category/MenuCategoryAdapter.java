/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.category;

import android.app.AlertDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 27.
 */
class MenuCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private MenuSettingViewModel menuSettingViewModel;
    private LifecycleOwner lifecycleOwner;
    private OnStartDragListener dragStartListener;

    private List<MenuCategory> menuCategoryList;

    MenuCategoryAdapter(final MenuSettingViewModel menuSettingViewModel,
                        final LifecycleOwner lifecycleOwner, final OnStartDragListener dragStartListener) {

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
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_menu_category_setting_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_menu_category_setting, parent, false);
            return new BodyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BodyViewHolder) {
            BodyViewHolder viewHolder = (BodyViewHolder) holder;
            MenuCategory menuCategory = menuCategoryList.get(position - 1);
            viewHolder.name.setText(menuCategory.name);
            viewHolder.reorder.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(holder);
                }
                return false;
            });
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            Context context = viewHolder.itemView.getContext();

            viewHolder.itemView.setOnClickListener(v -> {
                AppCompatEditText editText = new AppCompatEditText(context);

                new AlertDialog.Builder(context)
                        .setTitle(R.string.menu_category_setting_add_new_category_dialog_title)
                        .setMessage(R.string.menu_category_setting_add_new_category_dialog_message)
                        .setView(editText)
                        .setCancelable(true)
                        .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                        .setPositiveButton(R.string.button_text_apply, (dialog, which) -> {
                            int seqNum = menuCategoryList.size();
                            menuSettingViewModel.createCategory(editText.getText().toString(), seqNum)
                                    .observe(lifecycleOwner, this::onCreateMenuCategory);
                            dialog.dismiss();
                        })
                        .create()
                        .show();
            });
        }
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {
        if (toPosition == 0) {
            return;
        }

        if (fromPosition < toPosition) {
            for (int i = fromPosition - 1; i < toPosition - 1; i++) {
                Collections.swap(menuCategoryList, i, i + 1);
            }
        } else {
            for (int i = fromPosition - 1; i > toPosition - 1; i--) {
                Collections.swap(menuCategoryList, i, i - 1);
            }
        }

        for (MenuCategory menuCategory : menuCategoryList) {
            menuCategory.seqNum = menuCategoryList.indexOf(menuCategory);
        }

        notifyItemMoved(fromPosition, toPosition);

        menuSettingViewModel.updateCategorySeqNum(menuCategoryList);
    }

    @Override
    public void onItemDismiss(final int position) {
        //Empty Body
    }

    private void onCreateMenuCategory(MenuCategory menuCategory) {
        menuCategoryList.add(menuCategory);
        notifyItemInserted(menuCategoryList.size() - 1);
    }

    @Override
    public int getItemViewType(final int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return menuCategoryList.size() + 1;
    }

    static class BodyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.reorder) AppCompatImageView reorder;

        private BodyViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private HeaderViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
