/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.cart.CartActivity;
import com.mark.zumo.client.customer.view.menu.detail.MenuDetailActivity;
import com.mark.zumo.client.customer.view.order.OrderFragment;
import com.mark.zumo.client.customer.view.payment.PaymentActivity;
import com.mark.zumo.client.customer.viewmodel.MainViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 18.
 */
public class MainBodyFragment extends Fragment {

    private static final String TAG = "MainBodyFragment";
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;

    private MainViewModel mainViewModel;
    private TabPagerAdapter tabPagerAdapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_body, container, false);
        ButterKnife.bind(this, view);
        inflateTabLayout();
        return view;
    }

    private void inflateTabLayout() {
        tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(final TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab) {

            }
        });

        mainViewModel.findStore(getActivity())
                .observe(this, this::onD2dLoadStore);
    }

    private void onD2dLoadStore(Store store) {
        Log.d(TAG, "onD2dLoadStore: " + store);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab == null) {
            return;
        }
        tab.setText(store.name);
        tabPagerAdapter.onD2dLoadStore(store);
    }

    private void selectTabFragment(String className) {
        int itemPosition = tabPagerAdapter.getItemPosition(className);
        TabLayout.Tab tab = tabLayout.getTabAt(itemPosition);
        if (tab == null) {
            return;
        }
        tab.select();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case CartActivity.REQUEST_CODE:
                switch (resultCode) {
                    case CartActivity.RESULT_CODE_PAYMENT_SUCCESS:
                        String orderUuid = data.getStringExtra(PaymentActivity.KEY_ORDER_UUID);
                        onSuccessPayment(orderUuid);
                        break;

                    case CartActivity.RESULT_CODE_PAYMENT_FAILED:
                        break;
                }
                break;

            case MenuDetailActivity.REQUEST_CODE:
                switch (resultCode) {
                    case MenuDetailActivity.RESULT_CODE_PAYMENT_SUCCESS:
                        String orderUuid = data.getStringExtra(PaymentActivity.KEY_ORDER_UUID);
                        onSuccessPayment(orderUuid);
                        break;

                    case MenuDetailActivity.RESULT_CODE_PAYMENT_FAILED:
                        break;
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onSuccessPayment(String orderUuid) {
        mainViewModel.onSuccessPayment(orderUuid);
        selectTabFragment(OrderFragment.class.getName());
    }
}
