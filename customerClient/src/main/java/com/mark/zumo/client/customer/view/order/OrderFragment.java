/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.order;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.mark.zumo.client.core.entity.MenuOrder;
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

    private OrderViewModel orderViewModel;
    private OrderAdapter adapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
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
    public void onResume() {
        super.onResume();
        refreshOrderList();
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
