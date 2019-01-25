/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.store.view.order;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.order.canceled.CanceledOrderFragment;
import com.mark.zumo.client.store.view.order.complete.CompleteOrderFragment;
import com.mark.zumo.client.store.view.order.reuqested.RequestedOrderFragment;
import com.mark.zumo.client.store.viewmodel.MainViewModel;
import com.mark.zumo.client.store.viewmodel.OrderViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 7. 1.
 */
public class OrderFragment extends Fragment {

    public static final String TAG = "OrderFragment";
    @BindView(R.id.main_activity_background_image) AppCompatImageView backgroundImage;

    @BindView(R.id.requested_order_indicator_count) AppCompatTextView requestedOrderCount;
    @BindView(R.id.canceled_order_order_indicator_count) AppCompatTextView canceledOrderCount;
    @BindView(R.id.complete_order_indicator_count) AppCompatTextView completeOrderCount;

    @BindView(R.id.requested_order_indicator) ConstraintLayout requestedOrderIndicator;

    @BindView(R.id.requested_order_indicator_title) AppCompatTextView requestedOrderIndicatorTitle;
    @BindView(R.id.complete_order_indicator_title) AppCompatTextView completeOrderIndicatorTitle;
    @BindView(R.id.canceled_order_indicator_title) AppCompatTextView canceledOrderIndicatorTitle;

    private OrderViewModel orderViewModel;
    private MainViewModel mainViewModel;

    private AppCompatTextView selectedIndicator;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, view);
        inflateStoreInfo();
        inflateIndicators();
        return view;
    }

    private void inflateStoreInfo() {
        mainViewModel.getSessionStoreFlowable().observe(this, this::onLoadStore);
    }

    private void onLoadStore(Store store) {
        GlideApp.with(this)
                .load(store.coverImageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(backgroundImage);
    }

    private void inflateIndicators() {
        orderViewModel.requestedMenuOrderList().observe(this, this::onLoadRequestedOrder);
        orderViewModel.canceledMenuOrderList().observe(this, this::onLoadCanceledOrder);
        orderViewModel.completeMenuOrderList().observe(this, this::onLoadCompleteOrder);

        requestedOrderIndicator.performClick();
    }

    private void onLoadRequestedOrder(List<MenuOrder> requestedOrderList) {
        String text = String.valueOf(requestedOrderList.size());
        requestedOrderCount.setText(text);
    }

    private void onLoadCanceledOrder(List<MenuOrder> acceptedOrder) {
        String text = String.valueOf(acceptedOrder.size());
        canceledOrderCount.setText(text);
    }

    private void onLoadCompleteOrder(List<MenuOrder> completeOrder) {
        String text = String.valueOf(completeOrder.size());
        completeOrderCount.setText(text);
    }

    @OnClick({R.id.requested_order_indicator, R.id.canceled_order_indicator, R.id.complete_order_indicator})
    public void onViewClicked(View view) {
        if (selectedIndicator != null) {
            selectedIndicator.setTextColor(getContext().getResources().getColor(R.color.color_50));
            selectedIndicator.setTypeface(null, Typeface.NORMAL);
        }

        int id = view.getId();
        switch (id) {
            case R.id.requested_order_indicator:
                selectedIndicator = requestedOrderIndicatorTitle;
                break;
            case R.id.canceled_order_indicator:
                selectedIndicator = canceledOrderIndicatorTitle;
                break;
            case R.id.complete_order_indicator:
                selectedIndicator = completeOrderIndicatorTitle;
                break;
        }

        if (selectedIndicator != null) {
            selectedIndicator.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            selectedIndicator.setTypeface(null, Typeface.BOLD);
        }

        String fragmentName = null;
        switch (id) {
            case R.id.requested_order_indicator:
                fragmentName = RequestedOrderFragment.class.getName();
                break;
            case R.id.canceled_order_indicator:
                fragmentName = CanceledOrderFragment.class.getName();
                break;
            case R.id.complete_order_indicator:
                fragmentName = CompleteOrderFragment.class.getName();
                break;
        }
        Fragment targetFragment = Fragment.instantiate(getContext(), fragmentName);

        getFragmentManager().beginTransaction()
                .replace(R.id.order_content_fragment, targetFragment, String.valueOf(R.id.requested_order_indicator))
                .commit();

    }
}
