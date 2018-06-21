/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.order.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.OrderViewModel;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 6. 13.
 */
public class OrderDetailActivity extends AppCompatActivity {

    public final static String KEY_ORDER_UUID = "menu_order_uuid";
    public final static String KEY_STORE_UUID = "store_uuid";

    @BindView(R.id.store_cover_image) AppCompatImageView storeCoverImage;
    @BindView(R.id.back_button) AppCompatImageButton backButton;
    @BindView(R.id.store_cover_title) AppCompatTextView storeCoverTitle;
    @BindView(R.id.order_detail_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.total_price) TextView totalPrice;
    @BindView(R.id.cancel_order) AppCompatButton cancelOrder;

    private OrderViewModel orderViewModel;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);

        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);

        inflateStoreInfo();
        inflateOrderInfo();
        inflateOrderDetailRecyclerView();
    }

    private void inflateOrderDetailRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        OrderDetailAdapter adapter = new OrderDetailAdapter(orderViewModel, this);
        recyclerView.setAdapter(adapter);

        String orderUuid = getIntent().getStringExtra(KEY_ORDER_UUID);
        orderViewModel.getMenuOrderDetail(orderUuid).observe(this, adapter::setOrderDetailList);
    }

    private void inflateStoreInfo() {
        String storeUuid = getIntent().getStringExtra(KEY_STORE_UUID);
        orderViewModel.getStoreData(storeUuid).observe(this, this::onLoadStore);
    }

    private void onLoadStore(Store store) {
        storeCoverTitle.setText(store.name);
        GlideApp.with(this)
                .load(store.coverImageUrl)
                .apply(GlideUtils.storeCoverImageOptions())
                .transition(GlideUtils.storeCoverTransitionOptions());
    }

    private void inflateOrderInfo() {
        String orderUuid = getIntent().getStringExtra(KEY_ORDER_UUID);
        orderViewModel.getMenuOrder(orderUuid).observe(this, this::onLoadMenuOrder);
    }

    private void onLoadMenuOrder(MenuOrder menuOrder) {
        totalPrice.setText(NumberFormat.getCurrencyInstance().format(menuOrder.totalPrice));
        MenuOrder.State state = MenuOrder.State.of(menuOrder.state);
        cancelOrder.setVisibility(state == MenuOrder.State.PAYMENT_READY ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.cancel_order)
    public void onCancelOrderClicked() {
    }

    @OnClick(R.id.back_button)
    public void onBackButtonClicked() {
        finish();
    }
}
