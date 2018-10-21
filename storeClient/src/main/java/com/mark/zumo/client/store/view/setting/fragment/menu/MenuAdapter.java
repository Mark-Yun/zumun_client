/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.menu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.core.view.TouchResponse;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.fragment.menu.detail.MenuDetailSettingFragment;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 26.
 */
class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private final FragmentManager fragmentManager;

    private List<Menu> menuList;

    MenuAdapter(final FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        menuList = new ArrayList<>();
    }

    public void setMenuList(final List<Menu> menuList) {
        this.menuList = menuList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        Menu menu = menuList.get(position);
        viewHolder.name.setText(menu.name);
        viewHolder.price.setText(NumberFormat.getCurrencyInstance().format(menu.price));

        viewHolder.image.setVisibility(TextUtils.isEmpty(menu.imageUrl) ? View.GONE : View.VISIBLE);

        GlideApp.with(viewHolder.itemView.getContext())
                .load(menu.imageUrl)
                .apply(GlideUtils.menuImageOptions())
                .transition(GlideUtils.menuTransitionOptions())
                .into(viewHolder.image);

        viewHolder.itemView.setOnClickListener(v -> onClickMenu(v, menu));
    }

    private void onClickMenu(final View itemView, final Menu menu) {
        TouchResponse.big();

        Bundle bundle = new Bundle();
        bundle.putString(MenuDetailSettingFragment.KEY_MENU_UUID, menu.uuid);

        Fragment fragment = Fragment.instantiate(itemView.getContext(), MenuDetailSettingFragment.class.getName(), bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.menu_detail_fragment, fragment)
                .commit();
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.price) AppCompatTextView price;
        @BindView(R.id.image) AppCompatImageView image;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
