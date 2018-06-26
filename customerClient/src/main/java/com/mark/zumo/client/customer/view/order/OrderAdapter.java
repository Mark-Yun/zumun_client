/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.order;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.core.view.TouchResponse;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.order.detail.OrderDetailActivity;
import com.mark.zumo.client.customer.viewmodel.OrderViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 12.
 */
class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    public static final String TAG = "OrderAdapter";
    private LifecycleOwner lifecycleOwner;
    private OrderViewModel orderViewModel;

    private List<MenuOrder> orderList;

    OrderAdapter(final LifecycleOwner lifecycleOwner, final OrderViewModel orderViewModel) {
        this.lifecycleOwner = lifecycleOwner;
        this.orderViewModel = orderViewModel;

        orderList = new ArrayList<>();
    }

    void setOrderList(final List<MenuOrder> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_menu_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MenuOrder menuOrder = orderList.get(position);

        holder.orderNumber.setText(menuOrder.orderNumber);
        holder.orderTime.setText(menuOrder.createdDate);

        MenuOrder.State state = MenuOrder.State.of(menuOrder.state);
        holder.orderState.setText(state.stringRes);
        int color = holder.itemView.getContext().getResources().getColor(state.colorRes);
        holder.orderState.setBackgroundColor(color);

        orderViewModel.getStoreData(menuOrder.storeUuid).observe(lifecycleOwner, store -> {
            if (store == null) {
                return;
            }

            holder.title.setText(store.name);

            GlideApp.with(holder.image)
                    //TODO REMOVETEST DATA
//                    .load(store.thumbnailUrl)
                    .load(R.drawable.data_5_ice)
                    .apply(GlideUtils.storeCoverImageOptions())
                    .transition(GlideUtils.storeTransitionOptions())
                    .into(holder.image);
        });

        holder.itemView.setOnClickListener(v -> {
            TouchResponse.small();
            Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.KEY_ORDER_UUID, menuOrder.uuid);
            intent.putExtra(OrderDetailActivity.KEY_STORE_UUID, menuOrder.storeUuid);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) AppCompatTextView title;
        @BindView(R.id.order_number) AppCompatTextView orderNumber;
        @BindView(R.id.order_time) AppCompatTextView orderTime;
        @BindView(R.id.image) AppCompatImageView image;
        @BindView(R.id.order_state) AppCompatTextView orderState;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
