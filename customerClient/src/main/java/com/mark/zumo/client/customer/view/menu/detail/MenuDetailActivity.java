/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.Navigator;
import com.mark.zumo.client.customer.view.menu.detail.fragment.MenuInfoFragment;
import com.mark.zumo.client.customer.view.menu.detail.fragment.MenuOptionFragment;
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

    private String menuUuid;
    private String storeUuid;
    private String cartIndex;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);
        ButterKnife.bind(this);

        menuUuid = getIntent().getStringExtra(KEY_MENU_UUID);
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
        MenuDetailViewModel menuDetailViewModel = ViewModelProviders.of(this).get(MenuDetailViewModel.class);
        menuDetailViewModel.addToCartCurrentItems(storeUuid, menuUuid);
        finish();
    }

    @OnClick(R.id.place_order)
    void onClickSendOrder() {
        MenuDetailViewModel menuDetailViewModel = ViewModelProviders.of(this).get(MenuDetailViewModel.class);
        menuDetailViewModel.placeOrder(storeUuid, menuUuid).observe(this, this::onSuccessCreateOrder);
    }

    private void onSuccessCreateOrder(MenuOrder menuOrder) {
        Toast.makeText(this, "SUCCESS -" + menuOrder, Toast.LENGTH_SHORT).show();
        finish();
    }

    @OnClick(R.id.back_button)
    void onClickBackButton() {
        finish();
    }
}
