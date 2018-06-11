/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.store.view.requestedorder;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.view.Navigator;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.OrderViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 6. 5.
 */
public class RequestedOrderActivity extends AppCompatActivity {

    @BindView(R.id.requested_order_recycler_view) RecyclerView recyclerView;

    private OrderViewModel orderViewModel;

    private RequestedOrderAdapter adapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_order);
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
        ButterKnife.bind(this);
        inflateRecyclerView();
    }

    private void inflateRecyclerView() {
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layout);

        adapter = new RequestedOrderAdapter(orderViewModel, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setHasFixedSize(true);
        orderViewModel.requestedMenuOrderList().observe(this, this::onLoadRequestedMenuOrderList);
    }

    private void onLoadRequestedMenuOrderList(List<MenuOrder> menuOrderList) {
        adapter.setMenuOrderList(menuOrderList);
    }

    @OnClick(R.id.back_fab)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.accept_all)
    void onClickAcceptAll() {

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Navigator.setBlurLayoutVisible(true);
    }

    @Override
    public void finish() {
        super.finish();
        Navigator.setBlurLayoutVisible(false);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.accept_all)
    void acceptAll() {
        orderViewModel.acceptAllOrder();
        finish();
    }
}
