/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.cart;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.core.view.TouchResponse;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.payment.PaymentActivity;
import com.mark.zumo.client.customer.viewmodel.CartViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 5. 22.
 */
public class CartActivity extends AppCompatActivity {

    public static final String KEY_STORE_UUID = "store_uuid";

    public static final int REQUEST_CODE = 2;

    public static final int RESULT_CODE_PAYMENT_SUCCESS = PaymentActivity.RESULT_CODE_PAYMENT_SUCCESS;
    public static final int RESULT_CODE_PAYMENT_FAILED = PaymentActivity.RESULT_CODE_PAYMENT_FAILED;

    @BindView(R.id.store_cover_image) AppCompatImageView storeImage;
    @BindView(R.id.store_cover_title) AppCompatTextView storeTitle;
    @BindView(R.id.order_detail_recycler_view) RecyclerView cartItemRecyclerView;
    @BindView(R.id.total_price) AppCompatTextView totalPrice;

    private String storeUuid;
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeUuid = getIntent().getStringExtra(KEY_STORE_UUID);
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel.class);

        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        inflateView(storeUuid);
    }

    private void inflateView(String storeUuid) {
        cartViewModel.getStore(storeUuid).observe(this, this::onLoadStore);

        cartItemRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        cartItemRecyclerView.setLayoutManager(layoutManager);

        CartMenuAdapter cartMenuAdapter = new CartMenuAdapter(cartViewModel, this, storeUuid);
        cartItemRecyclerView.setAdapter(cartMenuAdapter);

        cartViewModel.getCartItemList(storeUuid).observe(this, cartMenuAdapter::setOrderDetailList);
        cartViewModel.getTotalPrice(storeUuid).observe(this, totalPrice::setText);
    }

    private void onLoadStore(Store store) {
        storeTitle.setText(store.name);

        GlideApp.with(this)
                .load(store.coverImageUrl)
                .transition(GlideUtils.storeCoverTransitionOptions())
                .apply(GlideUtils.storeCoverImageOptions())
                .into(storeImage);
    }

    @OnClick(R.id.back_button)
    void onClickBackButton() {
        TouchResponse.small();
        finish();
    }

    @OnClick(R.id.place_order)
    void onClickPlaceOrder() {
        TouchResponse.medium();
        cartViewModel.placeOrder(storeUuid).observe(this, this::onSuccessCreateOrder);
    }

    private void onSuccessCreateOrder(MenuOrder menuOrder) {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PaymentActivity.KEY_ORDER_UUID, menuOrder.uuid);
        startActivityForResult(intent, PaymentActivity.REQ_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PaymentActivity.REQ_CODE_PAYMENT:
                switch (resultCode) {
                    case PaymentActivity.RESULT_CODE_PAYMENT_SUCCESS:
                        setResult(RESULT_CODE_PAYMENT_SUCCESS, data);
                        cartViewModel.clearCartItem(storeUuid);
                        finish();
                        break;

                    case PaymentActivity.RESULT_CODE_PAYMENT_FAILED:
                        setResult(RESULT_CODE_PAYMENT_FAILED);
                        break;
                }
                break;
        }
    }
}
