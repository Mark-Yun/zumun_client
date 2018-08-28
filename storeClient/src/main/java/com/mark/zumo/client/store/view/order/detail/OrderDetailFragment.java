/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.order.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.util.DateUtil;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.OrderViewModel;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 7. 1.
 */
public class OrderDetailFragment extends Fragment {

    public static String KEY_ORDER_UUID = "order_uuid";

    @BindView(R.id.order_number) AppCompatTextView orderNumber;
    @BindView(R.id.order_date) AppCompatTextView orderDate;
    @BindView(R.id.order_time) AppCompatTextView orderTime;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.close_button) AppCompatImageButton closeButton;

    @BindView(R.id.reject) AppCompatButton reject;
    @BindView(R.id.accept) AppCompatButton accept;
    @BindView(R.id.refund) AppCompatButton refund;
    @BindView(R.id.complete) AppCompatButton complete;

    private OrderViewModel orderViewModel;
    private String orderUuid;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
        orderUuid = Objects.requireNonNull(getArguments()).getString(KEY_ORDER_UUID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requested_order_detail, container, false);
        ButterKnife.bind(this, view);
        inflateOrderInformation();
        inflateRecyclerView();
        inflateCloseButton();
        return view;
    }

    private void inflateCloseButton() {
        closeButton.setOnClickListener(v -> finish());
    }

    private void finish() {
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .remove(this)
                .commit();
    }

    private void inflateOrderInformation() {
        orderViewModel.getMenuOrderFromDisk(orderUuid).observe(this, this::onLoadMenuOrder);
    }

    private void inflateRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        OrderDetailAdapter adapter = new OrderDetailAdapter();
        recyclerView.setAdapter(adapter);

        orderViewModel.orderDetailList(orderUuid).observe(this, adapter::setOrderDetailList);
    }

    private void onLoadMenuOrder(MenuOrder menuOrder) {
        orderNumber.setText(menuOrder.orderNumber);
        orderDate.setText(DateUtil.getLocalDate(menuOrder.createdDate));
        orderTime.setText(DateUtil.getLocalTime(menuOrder.createdDate));

        MenuOrder.State state = MenuOrder.State.of(menuOrder.state);
        boolean isAccepted = MenuOrder.State.ACCEPTED == state;
        boolean isRequested = MenuOrder.State.REQUESTED == state;

        accept.setVisibility(isRequested ? View.VISIBLE : View.GONE);
        reject.setVisibility(isRequested ? View.VISIBLE : View.GONE);
        complete.setVisibility(isAccepted ? View.VISIBLE : View.GONE);
        refund.setVisibility(isAccepted ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.reject)
    public void onRejectClicked() {
        orderViewModel.rejectOrder(orderUuid);
        finish();
    }

    @OnClick(R.id.accept)
    public void onAcceptClicked() {
        orderViewModel.acceptOrder(orderUuid);
    }

    @OnClick(R.id.refund)
    public void onRefundClicked() {
        orderViewModel.refundOrder(orderUuid);
    }

    @OnClick(R.id.complete)
    public void onCompleteClicked() {
        orderViewModel.completeOrder(orderUuid);
        finish();
    }
}
