/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.category;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 8. 12.
 */
public class MenuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LifecycleOwner lifecycleOwner;
    private MenuSettingViewModel menuSettingViewModel;
    private MenuCategory menuCategory;

    private List<Menu> menuList;
    private Map<String, MenuDetail> menuDetailMap;
    private Set<String> checkedMenuList;
    private OnCheckedItemListChangeListener listChangeListener;

    MenuListAdapter(final LifecycleOwner lifecycleOwner,
                    final MenuCategory menuCategory,
                    final MenuSettingViewModel menuSettingViewModel) {

        this.lifecycleOwner = lifecycleOwner;
        this.menuCategory = menuCategory;
        this.menuSettingViewModel = menuSettingViewModel;

        menuList = new ArrayList<>();
        menuDetailMap = new HashMap<>();
        checkedMenuList = new HashSet<>();
    }

    List<String> getSelectedMenuUuidList() {
        List<String> list = new ArrayList<>();
        for (String menuUuid : checkedMenuList) {
            list.add(menuUuid);
        }

        return list;
    }

    String getCategoryUuid() {
        return menuCategory.uuid;
    }

    void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
        notifyDataSetChanged();
    }

    void setMenuDetailList(final List<MenuDetail> menuDetailList) {
        this.menuDetailMap = new HashMap<>();
        for (MenuDetail menuDetail : menuDetailList) {
            menuDetailMap.put(menuDetail.menuUuid, menuDetail);
        }
        checkedMenuList.clear();
        notifyDataSetChanged();
    }

    void setListChangeListener(OnCheckedItemListChangeListener listChangeListener) {
        this.listChangeListener = listChangeListener;
    }

    private boolean isListChanged() {
        Set<String> menuUuidSet = new HashSet<>();
        for (MenuDetail menuDetail : menuDetailMap.values()) {
            menuUuidSet.add(menuDetail.menuUuid);
        }

        for (String menuUuid : checkedMenuList) {
            if (!menuUuidSet.remove(menuUuid)) {
                return true;
            }
        }
        return !menuUuidSet.isEmpty();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            View view = layoutInflater.inflate(R.layout.card_view_category_setting_menu_list_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.card_view_category_setting_menu_list, parent, false);
            return new BodyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.categoryName.setText(menuCategory.name);
        } else if (holder instanceof BodyViewHolder) {
            BodyViewHolder viewHolder = (BodyViewHolder) holder;
            Menu menu = menuList.get(position - 1);
            AppCompatTextView checkedTextView = viewHolder.menuName;
            checkedTextView.setText(menu.name);
            AppCompatCheckBox checkBox = viewHolder.checkBox;
            boolean checked = menuDetailMap.containsKey(menu.uuid);
            checkBox.setChecked(checked);
            if (checked) {
                checkedMenuList.add(menu.uuid);
            }
            viewHolder.itemView.setOnClickListener(v -> checkBox.setChecked(!checkBox.isChecked()));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkedMenuList.add(menu.uuid);
                } else {
                    checkedMenuList.remove(menu.uuid);
                }
                if (listChangeListener != null) {
                    listChangeListener.onCheckedItemListChanged(isListChanged());
                }
            });
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int getItemCount() {
        return menuList.size() + 1;
    }

    @Override
    public int getItemViewType(final int position) {
        return position == 0 ? 0 : 1;
    }

    interface OnCheckedItemListChangeListener {
        void onCheckedItemListChanged(boolean isChanged);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.category_name) AppCompatTextView categoryName;

        HeaderViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class BodyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.menu_name) AppCompatTextView menuName;
        @BindView(R.id.check_box) AppCompatCheckBox checkBox;

        BodyViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
