/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu.detail.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.view.TouchResponse;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.menu.detail.MenuDetailActivity;
import com.mark.zumo.client.customer.viewmodel.MenuDetailViewModel;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 5. 24.
 */
public class MenuOptionFragment extends Fragment {

    public static final String TAG = "MenuOptionFragment";
    @BindView(R.id.menu_option_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.amount) AppCompatTextView amount;

    private String menuUuid;
    private String storeUuid;
    private int cartIndex;
    private boolean isModifyingCart;
    private MenuDetailViewModel menuDetailViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuDetailViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(MenuDetailViewModel.class);
        menuUuid = Objects.requireNonNull(getArguments()).getString(MenuDetailActivity.KEY_MENU_UUID);
        storeUuid = Objects.requireNonNull(getArguments()).getString(MenuDetailActivity.KEY_MENU_STORE_UUID);
        cartIndex = Objects.requireNonNull(getArguments()).getInt(MenuDetailActivity.KEY_CART_INDEX, -1);
        isModifyingCart = cartIndex > -1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_option, container, false);
        ButterKnife.bind(this, view);
        inflateAmount();
        inflateRecyclerView();
        if (isModifyingCart) {
            menuDetailViewModel.insertOrderDetailFromCart(storeUuid, cartIndex);
        }
        return view;
    }

    private void inflateRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        MenuOptionAdapter adapter = new MenuOptionAdapter(this, menuDetailViewModel);
        recyclerView.setAdapter(adapter);

        LiveData<Map<String, List<MenuOption>>> menuOptionMap = menuDetailViewModel.getMenuOptionMap(menuUuid);
        menuOptionMap.observe(this, adapter::setOptionMap);
    }

    private void inflateAmount() {
        menuDetailViewModel.menuAmount().observe(this, quantity -> amount.setText(String.valueOf(quantity)));
    }

    @OnClick(R.id.amount_plus_button)
    void onClickAmountPlus() {
        TouchResponse.medium();
        menuDetailViewModel.increaseAmount();
    }

    @OnClick(R.id.amount_minus_button)
    void onClickAmountMinus() {
        TouchResponse.medium();
        menuDetailViewModel.decreaseAmount();
    }
}
