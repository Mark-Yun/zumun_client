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

package com.mark.zumo.server.store.view.requestedorder;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.server.store.R;
import com.mark.zumo.server.store.viewmodel.OrderViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 5.
 */
class RequestedOrderAdapter extends RecyclerView.Adapter<RequestedOrderAdapter.ViewHolder> {

    private List<MenuOrder> menuOrderList;
    private OrderViewModel orderViewModel;
    private LifecycleOwner lifecycleOwner;

    RequestedOrderAdapter(final OrderViewModel orderViewModel, final LifecycleOwner lifecycleOwner) {
        this.orderViewModel = orderViewModel;
        this.lifecycleOwner = lifecycleOwner;
        menuOrderList = new ArrayList<>();
    }

    void setMenuOrderList(final List<MenuOrder> menuOrderList) {
        this.menuOrderList = menuOrderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_requested_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MenuOrder menuOrder = menuOrderList.get(position);

        holder.number.setText(menuOrder.orderNumber);
        holder.date.setText(menuOrder.createdDate.split(" ")[0]);
        holder.time.setText(menuOrder.createdDate.split(" ")[1]);
        holder.totalQuantity.setText(String.valueOf(menuOrder.totalQuantity));
        holder.totalPrice.setText(NumberFormat.getCurrencyInstance().format(menuOrder.totalPrice));
    }

    @Override
    public int getItemCount() {
        return menuOrderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.number) AppCompatTextView number;
        @BindView(R.id.date) AppCompatTextView date;
        @BindView(R.id.time) AppCompatTextView time;
        @BindView(R.id.total_quantity) AppCompatTextView totalQuantity;
        @BindView(R.id.total_price) AppCompatTextView totalPrice;
        @BindView(R.id.accept) AppCompatButton accept;
        @BindView(R.id.reject) AppCompatButton reject;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
