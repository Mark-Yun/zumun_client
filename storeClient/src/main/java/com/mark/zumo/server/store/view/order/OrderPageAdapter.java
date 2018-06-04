/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.server.store.view.order;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.server.store.R;
import com.mark.zumo.server.store.view.order.widget.TabLayoutSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 16.
 */
public class OrderPageAdapter extends RecyclerView.Adapter<OrderPageAdapter.ViewHolder>
        implements TabLayoutSupport.ViewPagerTabLayoutAdapter {

    private List<MenuOrder> menuOrderList = new ArrayList<>();

    void setMenuOrderList(final List<MenuOrder> menuOrderList) {
        this.menuOrderList = menuOrderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_menu_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MenuOrder menuOrder = getMenuOrder(position);
        holder.idTextView.setText("menu_uuid : " + menuOrder.uuid);
        holder.priceTextView.setText("menu_price : " + menuOrder.storeUuid);
    }

    private MenuOrder getMenuOrder(final int position) {
        return menuOrderList.get(position);
    }

    @Override
    public int getItemCount() {
        return menuOrderList.size();
    }

    @Override
    public String getPageTitle(final int i) {
        MenuOrder menuOrder = getMenuOrder(i);
        return "Order Number-" + menuOrder.uuid;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;

        @BindView(R.id.order_id) TextView idTextView;
        @BindView(R.id.order_price) TextView priceTextView;

        private ViewHolder(final View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
