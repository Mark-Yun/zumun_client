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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOptionCategory;
import com.mark.zumo.client.core.view.util.RecyclerUtils;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.SettingModeSelectee;
import com.mark.zumo.client.store.view.util.draghelper.reorder.DragNDropReorderHelperCallback;
import com.mark.zumo.client.store.view.util.draghelper.reorder.ItemTouchHelperAdapter;
import com.mark.zumo.client.store.view.util.draghelper.reorder.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
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
class MenuOptionSettingOptionListAdapter extends RecyclerView.Adapter<MenuOptionSettingOptionListAdapter.ViewHolder>
        implements ItemTouchHelperAdapter, SettingModeSelectee {

    private static final String TAG = "MenuOptionSettingOptionListAdapter";

    private final List<MenuOptionCategory> menuOptionCategoryList;
    private final MenuOptionSelectListener menuOptionSelectListener;
    private final HashSet<Runnable> modeUpdateOperationPool;
    private final Map<String, MenuOptionSettingOptionDetailListAdapter> optionDetailListAdapterMap;
    private final OnStartDragListener dragStartListener;

    private SettingMode settingMode = SettingMode.NONE;

    MenuOptionSettingOptionListAdapter(MenuOptionSelectListener menuOptionSelectListener,
                                       final OnStartDragListener dragStartListener) {
        this.menuOptionSelectListener = menuOptionSelectListener;

        modeUpdateOperationPool = new HashSet<>();
        menuOptionCategoryList = new ArrayList<>();
        optionDetailListAdapterMap = new HashMap<>();
        this.dragStartListener = dragStartListener;
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

    void onMenuOptionCategoryCreated(final MenuOptionCategory menuOptionCategory) {
        menuOptionCategoryList.add(menuOptionCategory);
        notifyItemInserted(menuOptionCategoryList.size() - 1);
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

    void setMenuOptionCategoryList(final List<MenuOptionCategory> menuOptionCategoryList) {
        this.menuOptionCategoryList.clear();
        this.menuOptionCategoryList.addAll(menuOptionCategoryList);
        notifyDataSetChanged();
    }

    void onCreateMenuCategory(final MenuOptionCategory menuOptionCategory) {
        this.menuOptionCategoryList.add(menuOptionCategory);
        notifyItemInserted(menuOptionCategoryList.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_menu_option_setting, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        MenuOptionCategory menuOptionCategory = menuOptionCategoryList.get(position);

        String name = menuOptionCategory.name;
        holder.name.setText(name);

        RecyclerView recyclerView = holder.optionRecyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        final MenuOptionSettingOptionDetailListAdapter menuOptionSettingOptionDetailListAdapter =
                new MenuOptionSettingOptionDetailListAdapter(getMenuOptionSelectListener());

        recyclerView.setAdapter(menuOptionSettingOptionDetailListAdapter);
        menuOptionSettingOptionDetailListAdapter.setMenuOptionList(menuOptionCategory.menuOptionList);
        optionDetailListAdapterMap.put(menuOptionCategory.uuid, menuOptionSettingOptionDetailListAdapter);

        ItemTouchHelper.Callback callback = new DragNDropReorderHelperCallback(menuOptionSettingOptionDetailListAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        menuOptionSettingOptionDetailListAdapter.setOnStartDragListener(itemTouchHelper::startDrag);

        holder.menuOptionHeader.setOnClickListener(v -> onClickItemView(holder, menuOptionCategory));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            menuOptionSelectListener.onSelectMenuOptionCategory(menuOptionCategory, isChecked);
            menuOptionSettingOptionDetailListAdapter.onClickParent(isChecked);
        });

        holder.reorder.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    dragStartListener.onStartDrag(holder);
                    break;
            }
            return false;
        });

        View.OnClickListener recyclerViewExpandClickListener = RecyclerUtils.recyclerViewExpandButton(recyclerView, holder.expandButton);
        holder.expandClickArea.setOnClickListener(recyclerViewExpandClickListener);

        Runnable modeUpdateOperation = () -> {
            holder.reorder.setVisibility(settingMode.isReorderMode() ? View.VISIBLE : View.GONE);
            holder.checkBox.setVisibility(settingMode.isDeleteMode() ? View.VISIBLE : View.GONE);
            menuOptionSettingOptionDetailListAdapter.setMode(settingMode);
        };
        modeUpdateOperation.run();
        modeUpdateOperationPool.add(modeUpdateOperation);
    }

    private void onClickItemView(final ViewHolder viewHolder, final MenuOptionCategory menuOptionCategory) {
        if (settingMode.isEditMode()) {
            menuOptionSelectListener.onModifyMenuOptionCategory(menuOptionCategory);
        } else if (settingMode.isDeleteMode()) {
            viewHolder.checkBox.performClick();
        } else {
            menuOptionSelectListener.onClickMenuOptionCategory(menuOptionCategory);
        }
    }

    @NonNull
    private MenuOptionSettingOptionDetailListAdapter.MenuOptionSelectListener getMenuOptionSelectListener() {
        return new MenuOptionSettingOptionDetailListAdapter.MenuOptionSelectListener() {
            @Override
            public void onClickMenuOption(final MenuOption menuOption) {
                menuOptionSelectListener.onClickMenuOption(menuOption);
            }

            @Override
            public void onModifyMenuOption(final MenuOption menuOption) {
                menuOptionSelectListener.onModifyMenuOption(menuOption);
            }

            @Override
            public void onSelectMenuOption(final MenuOption menuOption, final boolean isChecked) {
                menuOptionSelectListener.onSelectMenuOption(menuOption, isChecked);
            }

            @Override
            public void onReorderMenuOption(final List<MenuOption> menuOptionList) {
                menuOptionSelectListener.onReorderMenuOption(menuOptionList);
            }
        };
    }

    void onMenuOptionCategoryListRemoved(List<MenuOptionCategory> removedMenuOptionCategoryList) {
        for (MenuOptionCategory removedMenuOptionCategory : removedMenuOptionCategoryList) {
            List<MenuOptionCategory> tmpCategoryList = new ArrayList<>(this.menuOptionCategoryList);
            for (int i = 0; i < tmpCategoryList.size(); i++) {
                if (this.menuOptionCategoryList.get(i).uuid.equals(removedMenuOptionCategory.uuid)) {
                    this.menuOptionCategoryList.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }
    }

    void onMenuOptionListRemoved(List<MenuOption> removedMenuOptionList) {
        if (removedMenuOptionList == null || removedMenuOptionList.isEmpty()) {
            return;
        }

        String menuOptionCategoryUuid = removedMenuOptionList.get(0).menuOptionCategoryUuid;
        if (optionDetailListAdapterMap.containsKey(menuOptionCategoryUuid)) {
            optionDetailListAdapterMap.get(menuOptionCategoryUuid).onMenuOptionListRemoved(removedMenuOptionList);
        }
    }

    void onMenuOptionListUpdated(List<MenuOption> updatedMenuOptionList) {
        if (updatedMenuOptionList == null || updatedMenuOptionList.isEmpty()) {
            return;
        }

        String menuOptionCategoryUuid = updatedMenuOptionList.get(0).menuOptionCategoryUuid;
        if (optionDetailListAdapterMap.containsKey(menuOptionCategoryUuid)) {
            optionDetailListAdapterMap.get(menuOptionCategoryUuid).setMenuOptionList(updatedMenuOptionList);
        }
    }

    @Override
    public int getItemCount() {
        return menuOptionCategoryList.size();
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {
        Collections.swap(menuOptionCategoryList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(final int position) {
        //Empty Body
    }

    @Override
    public void onDrop() {
        for (MenuOptionCategory menuOptionCategory : this.menuOptionCategoryList) {
            menuOptionCategory.seqNum = this.menuOptionCategoryList.indexOf(menuOptionCategory);
        }
        menuOptionSelectListener.onReorderMenuOptionCategory(this.menuOptionCategoryList);
    }

    interface MenuOptionSelectListener extends MenuOptionSettingOptionDetailListAdapter.MenuOptionSelectListener {
        void onClickMenuOptionCategory(MenuOptionCategory menuOptionCategory);
        void onSelectMenuOptionCategory(MenuOptionCategory menuOptionCategory, boolean isChecked);
        void onReorderMenuOptionCategory(List<MenuOptionCategory> menuOptionCategoryList);
        void onModifyMenuOptionCategory(MenuOptionCategory menuOptionCategory);
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
