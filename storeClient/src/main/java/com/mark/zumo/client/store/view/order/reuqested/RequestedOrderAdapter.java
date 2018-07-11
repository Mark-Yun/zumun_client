/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.order.reuqested;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.util.DateUtil;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.order.detail.OrderDetailFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 7. 1.
 */
class RequestedOrderAdapter extends RecyclerView.Adapter<RequestedOrderAdapter.ViewHolder> {

    private FragmentManager fragmentManager;
    private List<MenuOrder> menuOrderList;

    RequestedOrderAdapter(final FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        menuOrderList = new ArrayList<>();
    }

    void setMenuOrderList(final List<MenuOrder> menuOrderList) {
        this.menuOrderList = menuOrderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestedOrderAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_requested_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestedOrderAdapter.ViewHolder holder, final int position) {
        MenuOrder menuOrder = menuOrderList.get(position);

        holder.amount.setText(String.valueOf(menuOrder.totalQuantity));
        holder.orderName.setText(menuOrder.orderName);
        holder.orderNumber.setText(menuOrder.orderNumber);
        int stateColor = ContextCompat.getColor(holder.itemView.getContext(), MenuOrder.State.of(menuOrder.state).colorRes);
        holder.orderNumber.setBackgroundColor(stateColor);
        holder.chronometer.setBase(DateUtil.getLocalTimeMills(menuOrder.createdDate));
        holder.chronometer.start();

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(OrderDetailFragment.KEY_ORDER_UUID, menuOrder.uuid);
            Fragment fragment = Fragment.instantiate(v.getContext(), OrderDetailFragment.class.getName(), bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.requested_order_detail_fragment, fragment)
                    .commit();
        });

        if (position == 0) {
            holder.itemView.performClick();
        }
    }

    @Override
    public int getItemCount() {
        return menuOrderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chronometer) Chronometer chronometer;
        @BindView(R.id.order_number) AppCompatTextView orderNumber;
        @BindView(R.id.order_name) AppCompatTextView orderName;
        @BindView(R.id.amount) AppCompatTextView amount;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
