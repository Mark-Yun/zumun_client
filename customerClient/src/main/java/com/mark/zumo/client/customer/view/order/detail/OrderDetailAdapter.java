/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.order.detail;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.database.entity.OrderDetail;
import com.mark.zumo.client.core.util.DateUtil;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.OrderViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 13.
 */
class OrderDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_BODY = 1;

    private final OrderViewModel orderViewModel;
    private final LifecycleOwner lifecycleOwner;

    private final List<OrderDetail> orderDetailList;

    OrderDetailAdapter(final OrderViewModel orderViewModel, final LifecycleOwner lifecycleOwner) {
        this.orderViewModel = orderViewModel;
        this.lifecycleOwner = lifecycleOwner;

        orderDetailList = new ArrayList<>();
    }

    void setOrderDetailList(final List<OrderDetail> orderDetailList) {
        this.orderDetailList.clear();
        this.orderDetailList.addAll(orderDetailList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_order_detail_header, parent, false);
                return new HeaderViewHolder(view);
            }
            case VIEW_TYPE_BODY: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_order_detail, parent, false);
                return new BodyViewHolder(view);
            }
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            if (orderDetailList.size() < 1) {
                return;
            }
            OrderDetail orderDetail = orderDetailList.get(0);

            orderViewModel.getMenuOrder(orderDetail.menuOrderUuid).observe(lifecycleOwner, menuOrder -> {
                if (menuOrder == null) {
                    return;
                }
                viewHolder.orderName.setText(menuOrder.orderName);
                viewHolder.orderNumber.setText(menuOrder.orderNumber);
                viewHolder.orderTime.setText(DateUtil.getLocalFormattedTime(menuOrder.createdDate));
                viewHolder.orderPrice.setText(NumberFormat.getCurrencyInstance().format(menuOrder.totalPrice));
            });
        } else if (holder instanceof BodyViewHolder) {
            BodyViewHolder viewHolder = (BodyViewHolder) holder;
            if (orderDetailList.size() < 1) {
                return;
            }
            OrderDetail orderDetail = orderDetailList.get(position - 1);

            viewHolder.menuName.setText(orderDetail.menuName);
            viewHolder.menuPrice.setText(NumberFormat.getCurrencyInstance().format(orderDetail.price));
            viewHolder.amount.setText(String.valueOf(orderDetail.quantity));

            viewHolder.recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(viewHolder.itemView.getContext());
            viewHolder.recyclerView.setLayoutManager(layoutManager);

            MenuOptionAdapter adapter = new MenuOptionAdapter();
            viewHolder.recyclerView.setAdapter(adapter);

            orderViewModel.getMenuOptionList(orderDetail.menuOptionUuidList).observe(lifecycleOwner, adapter::setMenuOptionList);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size() + 1;
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_BODY;
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.order_name) AppCompatTextView orderName;
        @BindView(R.id.order_number) AppCompatTextView orderNumber;
        @BindView(R.id.order_time) AppCompatTextView orderTime;
        @BindView(R.id.order_price) AppCompatTextView orderPrice;

        HeaderViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class BodyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.menu_name) AppCompatTextView menuName;
        @BindView(R.id.menu_price) AppCompatTextView menuPrice;
        @BindView(R.id.menu_option_recycler_view) RecyclerView recyclerView;
        @BindView(R.id.amount) AppCompatTextView amount;

        BodyViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
