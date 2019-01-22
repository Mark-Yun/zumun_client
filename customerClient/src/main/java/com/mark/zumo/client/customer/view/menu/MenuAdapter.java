/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.core.view.TouchResponse;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.menu.detail.MenuDetailActivity;
import com.mark.zumo.client.customer.view.rebound.Rebound;

import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 10.
 */
class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private final LifecycleOwner lifecycleOwner;

    private final MenuSelectListener listener;
    private final List<Menu> menuList;

    MenuAdapter(LifecycleOwner lifecycleOwner, final MenuSelectListener listener) {
        this.lifecycleOwner = lifecycleOwner;
        this.listener = listener;

        menuList = new CopyOnWriteArrayList<>();
    }

    void setMenuList(final List<Menu> menuList) {
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
        viewHolder.name.setText(menu.name);
        viewHolder.price.setText(NumberFormat.getCurrencyInstance().format(menu.price));

        if (!TextUtils.isEmpty(menu.imageUrl)) {
            viewHolder.image.setVisibility(View.VISIBLE);
            GlideApp.with(viewHolder.itemView.getContext())
                    .load(menu.imageUrl)
                    .apply(GlideUtils.menuImageOptions())
                    .transition(GlideUtils.menuTransitionOptions())
                    .into(viewHolder.image);
        }

        viewHolder.itemView.setOnClickListener(v -> onClickMenu(menu));
        viewHolder.itemView.setOnLongClickListener(v -> onLongClickMenu(v, menu));
        viewHolder.itemView.setOnTouchListener(Rebound.INSTANCE.onTouchListener(lifecycleOwner));
    }

    private void onClickMenu(final Menu menu) {
        TouchResponse.big();
        listener.onSelectMenu(menu);
    }

    private boolean onLongClickMenu(final View itemView, final Menu menu) {
        Context context = itemView.getContext();
        Intent intent = new Intent(context, MenuDetailActivity.class);
        intent.putExtra(MenuDetailActivity.KEY_MENU_UUID, menu.uuid);
        intent.putExtra(MenuDetailActivity.KEY_MENU_STORE_UUID, menu.storeUuid);
        ((FragmentActivity) context).startActivityForResult(intent, MenuDetailActivity.REQUEST_CODE);
        return true;
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public interface MenuSelectListener {
        void onSelectMenu(Menu menu);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.menu_container) ConstraintLayout container;
        @BindView(R.id.name) TextView name;
        @BindView(R.id.price) TextView price;
        @BindView(R.id.image) AppCompatImageView image;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
