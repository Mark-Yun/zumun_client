/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.order.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.repository.OrderRepository;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.app.fcm.CustomerMessageHandler;
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

    public static final String TAG = "OrderDetailFragment";
    private static final ArrayList<MenuOrder.State> STEPS = new ArrayList<MenuOrder.State>() {{
        add(MenuOrder.State.REQUESTED);
        add(MenuOrder.State.ACCEPTED);
        add(MenuOrder.State.COMPLETE);
        add(MenuOrder.State.FINISHED);
    }};
    @BindView(R.id.store_cover_image) AppCompatImageView storeCoverImage;
    @BindView(R.id.store_cover_title) AppCompatTextView storeCoverTitle;
    @BindView(R.id.order_detail_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.total_price) TextView totalPrice;
    @BindView(R.id.order_step_view) StepView orderStepView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.big_order_number) AppCompatTextView bigOrderNumber;
    @BindView(R.id.big_order_number_layout) ConstraintLayout bigOrderNumberLayout;
    @BindView(R.id.order_complete_information_title) AppCompatTextView orderCompleteInformationTitle;

    private OrderViewModel orderViewModel;
    private OrderDetailAdapter orderDetailAdapter;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
        registerOrderUpdateReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Context context = getContext();
        if (context == null) {
            return;
        }
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

    private void registerOrderUpdateReceiver() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                loadOrderInformation();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OrderRepository.ACTION_ORDER_UPDATED);
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        ButterKnife.bind(this, view);

        inflateOrderInfo();
        inflateOrderDetailRecyclerView();
        inflateSwipeRefreshLayout();

        loadOrderInformation();

        return view;
    }

    private void inflateSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
    }

    private void onRefresh() {
        loadOrderInformation();
    }

    private void inflateOrderDetailRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        orderDetailAdapter = new OrderDetailAdapter(orderViewModel, this);
        recyclerView.setAdapter(orderDetailAdapter);
    }

    private void loadOrderInformation() {
        if (getArguments() == null) {
            return;
        }

        String orderUuid = getArguments().getString(OrderDetailActivity.KEY_ORDER_UUID);
        orderViewModel.getMenuOrderDetail(orderUuid).observe(this, orderDetailAdapter::setOrderDetailList);
        orderViewModel.getMenuOrder(orderUuid).observe(this, this::onLoadMenuOrder);
        swipeRefreshLayout.setRefreshing(false);
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
        ArrayList<String> steps = new ArrayList<>();
        for (MenuOrder.State state : STEPS) {
            steps.add(getString(state.stringRes));
        }

        orderStepView.getState()
                .animationType(StepView.ANIMATION_ALL)
                .animationDuration(500)
                .steps(steps)
                .commit();
    }

    private void onLoadMenuOrder(MenuOrder menuOrder) {
        MenuOrder.State state = MenuOrder.State.of(menuOrder.state);
        boolean isComplete = state == MenuOrder.State.COMPLETE;

        totalPrice.setText(NumberFormat.getCurrencyInstance().format(menuOrder.totalPrice));
        orderStepView.go(STEPS.indexOf(state), true);
        orderStepView.setVisibility(isComplete ? View.GONE : View.VISIBLE);
        bigOrderNumberLayout.setVisibility(isComplete ? View.VISIBLE : View.GONE);
        bigOrderNumber.setText(menuOrder.orderNumber);
        Context context = getContext();
        if (context == null) {
            return;
        }

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.blink_animation);
        bigOrderNumber.setAnimation(animation);
        animation.start();

        if (isComplete) {
            Intent intent = new Intent(CustomerMessageHandler.VibrationContract.ACTION);
            intent.putExtra(CustomerMessageHandler.VibrationContract.ORDER_KEY, menuOrder.uuid);
            intent.setPackage(context.getPackageName());
            context.sendBroadcast(intent);
            Log.d(TAG, "onLoadMenuOrder: send broadcast-" + intent.getAction());
        }

        orderCompleteInformationTitle.setText(context.getString(R.string.order_complete_information_title, menuOrder.orderName));

        inflateStoreInfo(menuOrder.storeUuid);
    }

    @OnClick(R.id.back_button)
    public void onBackButtonClicked() {
        if (getActivity() == null) {
            return;
        }

        getActivity().finish();
    }
}
