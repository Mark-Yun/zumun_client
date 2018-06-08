/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.acceptedorder;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.acceptedorder.widget.TabLayoutSupport;
import com.mark.zumo.client.store.viewmodel.OrderViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 16.
 */
public class AcceptedOrderPageAdapter extends RecyclerView.Adapter<AcceptedOrderPageAdapter.ViewHolder>
        implements TabLayoutSupport.ViewPagerTabLayoutAdapter<AcceptedOrderPageAdapter.TabViewHolder> {

    private OrderViewModel orderViewModel;
    private LifecycleOwner lifecycleOwner;

    private List<MenuOrder> menuOrderList;
    private List<TabViewHolder> tabViewHolderList;

    AcceptedOrderPageAdapter(final OrderViewModel orderViewModel, final LifecycleOwner lifecycleOwner) {
        this.orderViewModel = orderViewModel;
        this.lifecycleOwner = lifecycleOwner;

        menuOrderList = new ArrayList<>();
        tabViewHolderList = new ArrayList<>();
    }

    void setMenuOrderList(final List<MenuOrder> menuOrderList) {
        this.menuOrderList = menuOrderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_accepted_order, parent, false);
        tabViewHolderList.add(createTabView(parent));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MenuOrder menuOrder = getMenuOrder(position);
        holder.number.setText(menuOrder.orderNumber);
        holder.date.setText(menuOrder.createdDate.split(" ")[0]);
        holder.time.setText(menuOrder.createdDate.split(" ")[1]);
        holder.totalQuantity.setText(String.valueOf(menuOrder.totalQuantity));
        holder.totalPrice.setText(NumberFormat.getCurrencyInstance().format(menuOrder.totalPrice));
        holder.complete.setOnClickListener(v -> orderViewModel.completeOrder(menuOrder));

        AcceptedOrderDetailAdapter adapter = new AcceptedOrderDetailAdapter(orderViewModel, lifecycleOwner);
        holder.recyclerView.setAdapter(adapter);

        LinearLayoutManager layout = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layout);

        orderViewModel.orderDetailList(menuOrder.uuid).observe(lifecycleOwner, adapter::setOrderDetailList);
    }

    private MenuOrder getMenuOrder(final int position) {
        return menuOrderList.get(position);
    }

    @Override
    public int getItemCount() {
        return menuOrderList.size();
    }

    @Override
    public TabViewHolder createTabView(@NonNull final ViewGroup parent) {
        View tabView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_order_tab, parent, false);
        return new TabViewHolder(tabView);
    }

    @Override
    public void bindTabView(@NonNull final TabViewHolder holder, final int position) {
        MenuOrder menuOrder = getMenuOrder(position);
        holder.number.setText(menuOrder.orderNumber);
        holder.time.setText(menuOrder.createdDate.split(" ")[1]);
        holder.quantity.setText(String.valueOf(menuOrder.totalQuantity));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.number) AppCompatTextView number;
        @BindView(R.id.date) AppCompatTextView date;
        @BindView(R.id.time) AppCompatTextView time;
        @BindView(R.id.total_quantity) AppCompatTextView totalQuantity;
        @BindView(R.id.total_price) AppCompatTextView totalPrice;
        @BindView(R.id.complete_button) AppCompatButton complete;
        @BindView(R.id.order_detail_recycler_view) RecyclerView recyclerView;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class TabViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.number) AppCompatTextView number;
        @BindView(R.id.time) AppCompatTextView time;
        @BindView(R.id.quantity) AppCompatTextView quantity;

        private TabViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
