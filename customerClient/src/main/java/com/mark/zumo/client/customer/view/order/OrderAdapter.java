/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.order;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.customer.R;
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

        holder.title.setText(menuOrder.name);
        holder.orderNumber.setText(menuOrder.orderNumber);
        holder.orderTime.setText(menuOrder.createdDate);

        MenuOrder.State state = MenuOrder.State.of(menuOrder.state);
        holder.orderState.setText(state.stringRes);
        Log.d(TAG, "onBindViewHolder: state.colorRes=" + state.colorRes);
        int color = holder.itemView.getContext().getResources().getColor(state.colorRes);
        holder.orderState.setBackgroundColor(color);

        orderViewModel.getStoreData(menuOrder.storeUuid).observe(lifecycleOwner, store -> {
            if (store == null) {
                return;
            }

            holder.subTitle.setText(store.name);

            GlideApp.with(holder.image)
                    .load(store.thumbnailUrl)
                    .apply(GlideUtils.storeCoverImageOptions())
                    .transition(GlideUtils.storeTransitionOptions())
                    .into(holder.image);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) AppCompatTextView title;
        @BindView(R.id.sub_title) AppCompatTextView subTitle;
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