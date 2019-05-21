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

package com.mark.zumo.client.store.view.setting.fragment.menu.menulist;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.store.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 26.
 */
class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private final List<Menu> menuList;
    private final MenuSelectListener menuSelectListener;

    MenuAdapter(MenuSelectListener menuSelectListener) {
        this.menuSelectListener = menuSelectListener;
        menuList = new CopyOnWriteArrayList<>();
    }

    void setMenuList(final List<Menu> menuList) {
        if (this.menuList.equals(menuList)) {
            return;
        }

        this.menuList.clear();
        this.menuList.addAll(menuList);

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
        if (menu == null) {
            return;
        }

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
        menuSelectListener.onSelectMenu(menu);
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

    interface MenuSelectListener {
        void onSelectMenu(Menu menu);
    }
}
