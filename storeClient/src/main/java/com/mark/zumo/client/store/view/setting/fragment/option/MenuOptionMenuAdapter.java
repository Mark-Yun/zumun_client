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

package com.mark.zumo.client.store.view.setting.fragment.option;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOptionDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 11. 11.
 */
class MenuOptionMenuAdapter extends RecyclerView.Adapter<MenuOptionMenuAdapter.ViewHolder> {

    private List<Menu> menuList;
    private List<MenuOptionDetail> menuOptionDetailList;

    MenuOptionMenuAdapter() {
        menuList = new ArrayList<>();
        menuOptionDetailList = new ArrayList<>();
    }

    void setMenuOptionDetailList(final List<MenuOptionDetail> menuOptionDetailList) {
        this.menuOptionDetailList = menuOptionDetailList;

        if (menuList.size() > 0) {
            notifyDataSetChanged();
        }
    }

    void setMenuList(final List<Menu> menuList) {

        this.menuList = menuList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
