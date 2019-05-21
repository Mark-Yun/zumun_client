/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.order;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.mark.zumo.client.core.database.entity.MenuOrder;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.OrderViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 6. 1.
 */
public class OrderFragment extends Fragment {

    @BindView(R.id.order_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.order_empty_text_view_layout) ConstraintLayout orderEmptyTextViewLayout;

    private OrderViewModel orderViewModel;
    private OrderAdapter adapter;

    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
        registerOrderUpdatedActionReceiver();
    }

    private void registerOrderUpdatedActionReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                if (!isAdded()) {
                    return;
                }
                refreshOrderList();
            }
        };
        IntentFilter intentFilter = new IntentFilter(OrderRepository.ACTION_ORDER_UPDATED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, view);
        inflateRecyclerView();
        inflateSwipeRefreshLayout();
        return view;
    }

    private void inflateSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshOrderList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private void inflateRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);

        adapter = new OrderAdapter(this, orderViewModel);
        recyclerView.setAdapter(adapter);

        orderViewModel.getMenuOrderList().observe(this, this::onLoadMenuOrderList);
    }

    private void onLoadMenuOrderList(List<MenuOrder> menuOrderList) {
        boolean isEmpty = menuOrderList.isEmpty();

        orderEmptyTextViewLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (adapter != null) {
            adapter.setOrderList(menuOrderList);
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        recyclerView.scheduleLayoutAnimation();
    }

    private void refreshOrderList() {
        orderViewModel.getMenuOrderList().observe(this, this::onLoadMenuOrderList);
    }
}
