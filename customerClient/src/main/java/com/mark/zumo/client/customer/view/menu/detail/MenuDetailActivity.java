/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.view.Navigator;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.TouchResponse;
import com.mark.zumo.client.customer.view.menu.detail.fragment.MenuInfoFragment;
import com.mark.zumo.client.customer.view.menu.detail.fragment.MenuOptionFragment;
import com.mark.zumo.client.customer.view.payment.PaymentActivity;
import com.mark.zumo.client.customer.viewmodel.MenuDetailViewModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 5. 25.
 */
public class MenuDetailActivity extends AppCompatActivity {

    public static final String KEY_MENU_UUID = "menu_uuid";
    public static final String KEY_MENU_STORE_UUID = "store_uuid";
    public static final String KEY_CART_INDEX = "cart_index";

    public static final int REQUEST_CODE = 1;

    public static final int RESULT_CODE_PAYMENT_SUCCESS = PaymentActivity.RESULT_CODE_PAYMENT_SUCCESS;
    public static final int RESULT_CODE_PAYMENT_FAILED = PaymentActivity.RESULT_CODE_PAYMENT_FAILED;

    private MenuDetailViewModel menuDetailViewModel;

    private Menu menu;

    private String storeUuid;
    private String cartIndex;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);
        ButterKnife.bind(this);

        menuDetailViewModel = ViewModelProviders.of(this).get(MenuDetailViewModel.class);

        String menuUuid = getIntent().getStringExtra(KEY_MENU_UUID);
        menuDetailViewModel.getMenu(menuUuid).observe(this, menu -> this.menu = menu);

        storeUuid = getIntent().getStringExtra(KEY_MENU_STORE_UUID);
        cartIndex = getIntent().getStringExtra(KEY_CART_INDEX);

        inflateViews(menuUuid);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void inflateViews(String menuUuid) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MENU_UUID, menuUuid);
        bundle.putString(KEY_CART_INDEX, cartIndex);

        Fragment menuInfoFragment = Fragment.instantiate(this, MenuInfoFragment.class.getName(), bundle);
        Fragment menuOptionFragment = Fragment.instantiate(this, MenuOptionFragment.class.getName(), bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.menu_info_fragment, menuInfoFragment)
                .add(R.id.menu_option_fragment, menuOptionFragment)
                .commit();
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

    @OnClick(R.id.add_to_cart_button)
    void onClickAddToCart() {
        TouchResponse.big();
        menuDetailViewModel = ViewModelProviders.of(this).get(MenuDetailViewModel.class);
        menuDetailViewModel.addToCartCurrentItems(storeUuid, menu);
        finish();
    }

    @OnClick(R.id.place_order)
    void onClickPlaceOrder() {
        TouchResponse.medium();
        menuDetailViewModel.placeOrder(storeUuid, menu).observe(this, this::onSuccessCreateOrder);
    }

    @OnClick(R.id.back_button)
    void onClickBackButton() {
        TouchResponse.small();
        finish();
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
                        break;

                    case PaymentActivity.RESULT_CODE_PAYMENT_FAILED:
                        setResult(RESULT_CODE_PAYMENT_FAILED);
                        break;
                }
                break;
        }
    }
}
