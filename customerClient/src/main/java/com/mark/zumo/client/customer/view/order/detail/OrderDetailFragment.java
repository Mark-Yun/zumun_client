/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.order.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.OrderViewModel;
import com.shuhart.stepview.StepView;

import java.text.NumberFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 9. 2.
 */
public class OrderDetailFragment extends Fragment {

    @BindView(R.id.store_cover_image) AppCompatImageView storeCoverImage;
    @BindView(R.id.store_cover_title) AppCompatTextView storeCoverTitle;
    @BindView(R.id.order_detail_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.total_price) TextView totalPrice;
    @BindView(R.id.order_step_view) StepView orderStepView;

    private OrderViewModel orderViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        ButterKnife.bind(this, view);

        inflateOrderInfo();
        inflateOrderDetailRecyclerView();
        inflateStepView();

        return view;
    }

    private void inflateStepView() {
    }

    private void inflateOrderDetailRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        OrderDetailAdapter adapter = new OrderDetailAdapter(orderViewModel, this);
        recyclerView.setAdapter(adapter);

        String orderUuid = getArguments().getString(OrderDetailActivity.KEY_ORDER_UUID);
        orderViewModel.getMenuOrderDetail(orderUuid).observe(this, adapter::setOrderDetailList);
    }

    private void inflateStoreInfo(String storeUuid) {
        orderViewModel.getStoreData(storeUuid).observe(this, this::onLoadStore);
    }

    private void onLoadStore(Store store) {
        storeCoverTitle.setText(store.name);
        GlideApp.with(this)
                .load(store.coverImageUrl)
                .apply(GlideUtils.storeCoverImageOptions())
                .transition(GlideUtils.storeCoverTransitionOptions())
                .into(storeCoverImage);
    }

    private void inflateOrderInfo() {
        String orderUuid = getArguments().getString(OrderDetailActivity.KEY_ORDER_UUID);

        ArrayList<String> steps = new ArrayList<String>() {{
            add(getString(MenuOrder.State.CREATED.stringRes));
            add(getString(MenuOrder.State.REQUESTED.stringRes));
            add(getString(MenuOrder.State.ACCEPTED.stringRes));
            add(getString(MenuOrder.State.COMPLETE.stringRes));
        }};
        orderStepView.getState()
                .animationType(StepView.ANIMATION_ALL)
                .animationDuration(500)
                .steps(steps)
                .commit();

        orderViewModel.getMenuOrder(orderUuid).observe(this, this::onLoadMenuOrder);
    }

    private void onLoadMenuOrder(MenuOrder menuOrder) {
        totalPrice.setText(NumberFormat.getCurrencyInstance().format(menuOrder.totalPrice));
        MenuOrder.State state = MenuOrder.State.of(menuOrder.state);
        orderStepView.go(state.ordinal(), true);

        inflateStoreInfo(menuOrder.storeUuid);
    }

    @OnClick(R.id.back_button)
    public void onBackButtonClicked() {
        getActivity().finish();
    }
}
