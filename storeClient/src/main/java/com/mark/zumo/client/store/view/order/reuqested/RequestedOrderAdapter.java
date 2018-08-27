/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.order.reuqested;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOrder;
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

    private static final String[] FRAGMENT_TAGS = {"ORDER_DETAIL_1", "ORDER_DETAIL_2"};
    private static final int[] FRAGMENT_IDS = {R.id.requested_order_detail_fragment_1, R.id.requested_order_detail_fragment_2};

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

        holder.orderName.setText(menuOrder.orderName);
        holder.orderNumber.setText(menuOrder.orderNumber);
        boolean isAccepted = menuOrder.state == MenuOrder.State.ACCEPTED.ordinal();
        holder.acceptedState.setVisibility(isAccepted ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(OrderDetailFragment.KEY_ORDER_UUID, menuOrder.uuid);
            Fragment fragment = Fragment.instantiate(v.getContext(), OrderDetailFragment.class.getName(), bundle);

            int index = findEmptyFragment();
            if (index > -1) {
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(FRAGMENT_IDS[index], fragment, FRAGMENT_TAGS[index])
                        .commit();
                Context context = holder.itemView.getContext();
                holder.orderName.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.orderName.setTypeface(null, Typeface.BOLD);
                holder.orderNumber.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.orderNumber.setTypeface(null, Typeface.BOLD);
                fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                    @Override
                    public void onFragmentDetached(final FragmentManager fm, final Fragment f) {
                        super.onFragmentDetached(fm, f);
                        if (f == fragment) {
                            if (holder.orderName != null) {
                                holder.orderName.setTextColor(context.getResources().getColor(R.color.colorTextLight));
                                holder.orderName.setTypeface(null, Typeface.NORMAL);
                            }

                            if (holder.orderNumber != null) {
                                holder.orderNumber.setTextColor(context.getResources().getColor(R.color.colorTextLight));
                                holder.orderNumber.setTypeface(null, Typeface.NORMAL);
                            }
                            fragmentManager.unregisterFragmentLifecycleCallbacks(this);
                        }
                    }
                }, true);
            }
        });
    }

    private int findEmptyFragment() {
        for (int i = 0; i < FRAGMENT_TAGS.length; i++) {
            if (fragmentManager.findFragmentByTag(FRAGMENT_TAGS[i]) == null) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return menuOrderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.order_number) AppCompatTextView orderNumber;
        @BindView(R.id.order_name) AppCompatTextView orderName;
        @BindView(R.id.accepted_state) AppCompatTextView acceptedState;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
