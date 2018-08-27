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
import android.support.annotation.Nullable;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final String TAG = "RequestedOrderAdapter";
    private Map<String, Fragment> fragmentMap;

    RequestedOrderAdapter(final FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        menuOrderList = new ArrayList<>();
        fragmentMap = new ConcurrentHashMap<>();
    }

    private static void setSelectedText(final @Nullable AppCompatTextView textView, boolean selected) {
        if (textView == null) {
            return;
        }
        Context context = textView.getContext();
        textView.setTextColor(context.getResources().getColor(selected ? R.color.colorAccent : R.color.colorTextLight));
        textView.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
    }

    void setMenuOrderList(final List<MenuOrder> menuOrderList) {
        this.menuOrderList = menuOrderList;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (String fragmentTag : FRAGMENT_TAGS) {
            Fragment fragmentByTag = fragmentManager.findFragmentByTag(fragmentTag);
            if (fragmentByTag == null) {
                continue;
            }

            fragmentTransaction.remove(fragmentByTag);
        }
        fragmentTransaction.commit();
        fragmentMap.clear();

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
            if (fragmentMap.containsKey(menuOrder.uuid)) {
                Fragment fragment = fragmentMap.get(menuOrder.uuid);
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .remove(fragment)
                        .commit();
                return;
            }

            int index = findEmptyFragment();
            if (index < 0) {
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString(OrderDetailFragment.KEY_ORDER_UUID, menuOrder.uuid);
            Fragment fragment = Fragment.instantiate(v.getContext(), OrderDetailFragment.class.getName(), bundle);

            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(FRAGMENT_IDS[index], fragment, FRAGMENT_TAGS[index])
                    .runOnCommit(() -> fragmentMap.put(menuOrder.uuid, fragment))
                    .commit();

            setSelectedText(holder.orderName, true);
            setSelectedText(holder.orderNumber, true);

            fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentViewDestroyed(final FragmentManager fm, final Fragment f) {
                    super.onFragmentViewDestroyed(fm, f);
                    if (f == fragment) {
                        setSelectedText(holder.orderName, false);
                        setSelectedText(holder.orderNumber, false);
                        fragmentManager.unregisterFragmentLifecycleCallbacks(this);
                        fragmentMap.remove(menuOrder.uuid);
                    }
                }
            }, true);
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
