/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.order.detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.database.entity.OrderDetail;
import com.mark.zumo.client.store.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 7. 1.
 */
class OrderDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_BODY = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    private List<OrderDetail> orderDetailList;

    OrderDetailAdapter() {
        orderDetailList = new ArrayList<>();
    }

    void setOrderDetailList(final List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_order_detail_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == VIEW_TYPE_BODY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_order_detail_body, parent, false);
            return new BodyViewHolder(view);
        } else if (viewType == VIEW_TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_order_detail_footer, parent, false);
            return new FooterViewHolder(view);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {

        } else if (holder instanceof BodyViewHolder) {
            OrderDetail orderDetail = orderDetailList.get(position - 1);

            BodyViewHolder viewHolder = (BodyViewHolder) holder;
            viewHolder.menu.setText(orderDetail.menuName);
            viewHolder.amount.setText(String.valueOf(orderDetail.quantity));
            viewHolder.price.setText(NumberFormat.getCurrencyInstance().format(orderDetail.price));
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder viewHolder = (FooterViewHolder) holder;
            int totalAmount = 0;
            int totalPrice = 0;
            for (OrderDetail orderDetail : orderDetailList) {
                totalAmount += orderDetail.quantity;
                totalPrice += orderDetail.price;
            }

            viewHolder.amount.setText(String.valueOf(totalAmount));
            viewHolder.price.setText(NumberFormat.getCurrencyInstance().format(totalPrice));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == VIEW_TYPE_HEADER) {
            return VIEW_TYPE_HEADER;
        } else if (position == orderDetailList.size() + 1) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_BODY;
        }
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size() + 2;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private HeaderViewHolder(final View itemView) {
            super(itemView);
        }
    }

    static class BodyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.menu) AppCompatTextView menu;
        @BindView(R.id.amount) AppCompatTextView amount;
        @BindView(R.id.price) AppCompatTextView price;

        private BodyViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.amount) AppCompatTextView amount;
        @BindView(R.id.price) AppCompatTextView price;

        FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
