/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.order.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mark.zumo.client.customer.R;

/**
 * Created by mark on 18. 6. 13.
 */
public class OrderDetailActivity extends AppCompatActivity {

    public final static String KEY_ORDER_UUID = "menu_order_uuid";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);


        Fragment orderDetailFragment = Fragment.instantiate(this, OrderDetailFragment.class.getName(), getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.order_detail_fragment, orderDetailFragment)
                .commit();
    }
}
