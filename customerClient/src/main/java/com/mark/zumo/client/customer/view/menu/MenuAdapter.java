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
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
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
import com.mark.zumo.client.customer.view.payment.PaymentActivity;
import com.mark.zumo.client.customer.view.rebound.Rebound;
import com.mark.zumo.client.customer.viewmodel.MenuViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 10.
 */
class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private LifecycleOwner lifecycleOwner;
    private MenuViewModel menuViewModel;

    private List<Menu> menuList;

    MenuAdapter(LifecycleOwner lifecycleOwner, MenuViewModel menuViewModel) {
        menuList = new ArrayList<>();
        this.lifecycleOwner = lifecycleOwner;
        this.menuViewModel = menuViewModel;
    }

    public void setMenuList(final List<Menu> menuList) {
        this.menuList = menuList;
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

        GlideApp.with(viewHolder.itemView.getContext())
                .load(menu.imageUrl)
                .apply(GlideUtils.menuImageOptions())
                .transition(GlideUtils.menuTransitionOptions())
                .into(viewHolder.image);

        viewHolder.itemView.setOnClickListener(v -> onClickMenu(v, menu));
        viewHolder.itemView.setOnLongClickListener(v -> onLongClickMenu(v, menu));
        viewHolder.itemView.setOnTouchListener(Rebound.INSTANCE.onTouchListener(lifecycleOwner));
    }

    private void onClickMenu(final View itemView, final Menu menu) {
        TouchResponse.big();
        menuViewModel.addMenuToCart(menu);
        onAddCartComplete(itemView, menu);
    }

    private void onAddCartComplete(final View itemView, final Menu menu) {
        String text = itemView.getContext().getString(R.string.added_to_cart, menu.name);
        Snackbar.make(itemView, text, Snackbar.LENGTH_LONG).show();
    }

    private boolean onLongClickMenu(final View itemView, final Menu menu) {

        Context context = itemView.getContext();
        Intent intent = new Intent(context, MenuDetailActivity.class);
        intent.putExtra(MenuDetailActivity.KEY_MENU_UUID, menu.uuid);
        intent.putExtra(MenuDetailActivity.KEY_MENU_STORE_UUID, menu.storeUuid);
        ((FragmentActivity) context).startActivityForResult(intent, PaymentActivity.REQ_CODE_PAYMENT);
        return true;
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) TextView name;
        @BindView(R.id.price) TextView price;
        @BindView(R.id.image) AppCompatImageView image;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
