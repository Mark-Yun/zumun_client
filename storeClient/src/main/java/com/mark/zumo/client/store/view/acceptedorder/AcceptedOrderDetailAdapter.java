/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.acceptedorder;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.OrderViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 6.
 */
class AcceptedOrderDetailAdapter extends RecyclerView.Adapter<AcceptedOrderDetailAdapter.ViewHolder> {

    private final OrderViewModel orderViewModel;
    private final LifecycleOwner lifecycleOwner;

    private List<OrderDetail> orderDetailList;

    AcceptedOrderDetailAdapter(final OrderViewModel orderViewModel, final LifecycleOwner lifecycleOwner) {
        this.orderViewModel = orderViewModel;
        this.lifecycleOwner = lifecycleOwner;

        orderDetailList = new ArrayList<>();
    }

    void setOrderDetailList(final List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_accepted_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        OrderDetail orderDetail = orderDetailList.get(position);
        holder.name.setText(orderDetail.menuName);
        holder.quantity.setText(String.valueOf(orderDetail.quantity));
        holder.price.setText(NumberFormat.getCurrencyInstance().format(orderDetail.price));

        AcceptedOrderOptionAdapter adapter = new AcceptedOrderOptionAdapter(orderViewModel, lifecycleOwner);
        holder.recyclerView.setAdapter(adapter);

        LinearLayoutManager layout = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layout);

        orderViewModel.menuOptionList(orderDetail.menuOptionUuidList).observe(lifecycleOwner, adapter::setMenuOptionList);
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.quantity) AppCompatTextView quantity;
        @BindView(R.id.price) AppCompatTextView price;
        @BindView(R.id.accepted_order_card_view_menu_option_recycler_view) RecyclerView recyclerView;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
