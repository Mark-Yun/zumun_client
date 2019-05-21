/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mark.zumo.client.core.database.entity.MenuOrder;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.core.view.RapidClickGuard;
import com.mark.zumo.client.core.view.TouchResponse;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.menu.detail.fragment.MenuInfoFragment;
import com.mark.zumo.client.customer.view.menu.detail.fragment.MenuOptionFragment;
import com.mark.zumo.client.customer.view.payment.PaymentActivity;
import com.mark.zumo.client.customer.viewmodel.MenuDetailViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.blurry.Blurry;

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

    private static ViewGroup blurredViewGroup;

    @BindView(R.id.add_to_cart_button) AppCompatButton addToCartButton;
    @BindView(R.id.place_order) AppCompatButton placeOrder;

    private MenuDetailViewModel menuDetailViewModel;
    private MenuOptionFragment menuOptionFragment;
    private int cartIndex;
    private String menuUuid;

    public static void startMenuDetailActivity(@Nullable ViewGroup viewGroup, String storeUuid, String menuUuid, int cartIndex) {
        if (viewGroup == null) {
            return;
        }

        MenuDetailActivity.blurredViewGroup = viewGroup;
        Context context = viewGroup.getContext();

        Intent intent = new Intent();
        intent.setClass(context, MenuDetailActivity.class);
        intent.putExtra(MenuDetailActivity.KEY_MENU_STORE_UUID, storeUuid);
        intent.putExtra(MenuDetailActivity.KEY_MENU_UUID, menuUuid);
        intent.putExtra(MenuDetailActivity.KEY_CART_INDEX, cartIndex);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startMenuDetailActivity(ViewGroup viewGroup, String storeUuid, String menuUuid) {
        MenuDetailActivity.startMenuDetailActivity(viewGroup, storeUuid, menuUuid, -1);
    }

    private static void setBlurredViewGroup(final ViewGroup blurredViewGroup) {
        MenuDetailActivity.blurredViewGroup = blurredViewGroup;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);
        ButterKnife.bind(this);

        menuDetailViewModel = ViewModelProviders.of(this).get(MenuDetailViewModel.class);
        menuUuid = getIntent().getStringExtra(KEY_MENU_UUID);
        cartIndex = getIntent().getIntExtra(KEY_CART_INDEX, -1);

        inflateViews();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void inflateViews() {
        Fragment menuInfoFragment = Fragment.instantiate(this, MenuInfoFragment.class.getName(), getIntent().getExtras());
        menuOptionFragment = (MenuOptionFragment) Fragment.instantiate(this, MenuOptionFragment.class.getName(), getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_info_fragment, menuInfoFragment)
                .replace(R.id.menu_option_fragment, menuOptionFragment)
                .commit();

        boolean isUpdateCartItem = cartIndex > -1;

        addToCartButton.setVisibility(isUpdateCartItem ? View.GONE : View.VISIBLE);
        placeOrder.setVisibility(isUpdateCartItem ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Blurry.with(blurredViewGroup.getContext())
                .radius(30)
                .sampling(3)
                .onto(blurredViewGroup);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Blurry.delete(blurredViewGroup);
        setBlurredViewGroup(null);
    }

    @Override
    public void finish() {
        super.finish();
        if (cartIndex > -1) {
            List<String> selectMenuOptionUuidList = menuOptionFragment.getSelectMenuOptionUuidList();
            int amount = menuOptionFragment.getAmount();
            menuDetailViewModel.updateToCartCurrentItems(menuUuid, selectMenuOptionUuidList, amount, cartIndex);
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.add_to_cart_button)
    void onClickAddToCart() {
        TouchResponse.big();
        List<String> selectMenuOptionUuidList = menuOptionFragment.getSelectMenuOptionUuidList();
        int amount = menuOptionFragment.getAmount();
        menuDetailViewModel.addToCartCurrentItems(menuUuid, selectMenuOptionUuidList, amount)
                .observe(this, orderDetail -> onSuccessAddMenuInCart());
    }

    private void onSuccessAddMenuInCart() {
        Toast.makeText(ContextHolder.getContext(), R.string.toast_add_to_cart_item_succeed, Toast.LENGTH_SHORT).show();
        finish();
    }


    @OnClick(R.id.place_order)
    void onClickPlaceOrder() {
        if (RapidClickGuard.shouldBlock(placeOrder, 2000)) {
            return;
        }
        TouchResponse.medium();
        List<String> selectMenuOptionUuidList = menuOptionFragment.getSelectMenuOptionUuidList();
        int amount = menuOptionFragment.getAmount();
        menuDetailViewModel.placeOrder(menuUuid, selectMenuOptionUuidList, amount).observe(this, this::onSuccessCreateOrder);
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
