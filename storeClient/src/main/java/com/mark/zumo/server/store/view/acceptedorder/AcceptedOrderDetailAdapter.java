/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.server.store.view.acceptedorder;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.server.store.R;
import com.mark.zumo.server.store.viewmodel.OrderViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 6.
 */
class AcceptedOrderDetailAdapter extends RecyclerView.Adapter<AcceptedOrderDetailAdapter.ViewHolder> {

    private OrderViewModel orderViewModel;
    private LifecycleOwner lifecycleOwner;

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
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.quantity) AppCompatTextView quantity;
        @BindView(R.id.price) AppCompatTextView price;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
