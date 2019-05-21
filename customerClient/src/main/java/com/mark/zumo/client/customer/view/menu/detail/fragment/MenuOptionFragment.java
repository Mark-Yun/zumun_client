/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu.detail.fragment;

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

import com.mark.zumo.client.core.database.entity.MenuOption;
import com.mark.zumo.client.core.database.entity.MenuOptionCategory;
import com.mark.zumo.client.core.database.entity.OrderDetail;
import com.mark.zumo.client.core.view.TouchResponse;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.menu.detail.MenuDetailActivity;
import com.mark.zumo.client.customer.viewmodel.MenuDetailViewModel;

import java.util.ArrayList;
import java.util.HashMap;
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
    @BindView(R.id.amount) AppCompatTextView amountTextView;

    private MenuDetailViewModel menuDetailViewModel;

    private String menuUuid;
    private String storeUuid;
    private int cartIndex;
    private int amount;
    private boolean isModifyingCart;
    private List<String> selectSingleMenuOptionUuidList;
    private Map<String, String> selectMultiMenuOptionUuidMap;
    private MenuOptionAdapter menuOptionAdapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuDetailViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(MenuDetailViewModel.class);
        menuUuid = Objects.requireNonNull(getArguments()).getString(MenuDetailActivity.KEY_MENU_UUID);
        storeUuid = Objects.requireNonNull(getArguments()).getString(MenuDetailActivity.KEY_MENU_STORE_UUID);
        cartIndex = Objects.requireNonNull(getArguments()).getInt(MenuDetailActivity.KEY_CART_INDEX, -1);
        isModifyingCart = cartIndex > -1;

        selectSingleMenuOptionUuidList = new ArrayList<>();
        selectMultiMenuOptionUuidMap = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_option, container, false);
        ButterKnife.bind(this, view);
        inflateAmount(1);
        inflateRecyclerView();
        if (isModifyingCart) {
            menuDetailViewModel.insertOrderDetailFromCart(storeUuid, cartIndex)
                    .observe(this, this::insertMenuOptionDataFromOrderDetail);
        }
        return view;
    }

    private void insertMenuOptionDataFromOrderDetail(OrderDetail orderDetail) {
        inflateAmount(orderDetail.quantity);
        selectSingleMenuOptionUuidList.addAll(orderDetail.menuOptionUuidList);
        menuOptionAdapter.setSelectedMenuOptionUuidList(orderDetail.menuOptionUuidList);
    }

    private void inflateRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        menuOptionAdapter = new MenuOptionAdapter(getMenuOptionSelectListener());
        recyclerView.setAdapter(menuOptionAdapter);
        menuDetailViewModel.getMenuOptionCategoryList(menuUuid).observe(this, menuOptionAdapter::setMenuOptionCategoryList);
    }

    public List<String> getSelectMenuOptionUuidList() {
        ArrayList<String> resultList = new ArrayList<>(selectSingleMenuOptionUuidList);
        resultList.addAll(selectMultiMenuOptionUuidMap.values());
        return resultList;
    }

    public int getAmount() {
        return amount;
    }

    @NonNull
    private MenuOptionAdapter.MenuOptionSelectListener getMenuOptionSelectListener() {
        return new MenuOptionAdapter.MenuOptionSelectListener() {
            @Override
            public void onSingleMenuOptionSelected(final MenuOption menuOption, final boolean isChecked) {
                if (isChecked) {
                    selectSingleMenuOptionUuidList.add(menuOption.uuid);
                } else {
                    selectSingleMenuOptionUuidList.remove(menuOption.uuid);
                }
            }

            @Override
            public void onMultiMenuOptionSelected(final MenuOption menuOption) {
                selectMultiMenuOptionUuidMap.put(menuOption.menuOptionCategoryUuid, menuOption.uuid);
            }

            @Override
            public void onMenuOptionCategoryCanceled(final MenuOptionCategory menuOptionCategory) {
                selectMultiMenuOptionUuidMap.remove(menuOptionCategory.uuid);
            }
        };
    }

    private void inflateAmount(int amount) {
        this.amount = amount;
        amountTextView.setText(String.valueOf(this.amount));
    }

    @OnClick(R.id.amount_plus_button)
    void onClickAmountPlus() {
        TouchResponse.medium();
        amountTextView.setText(String.valueOf(++amount));
    }

    @OnClick(R.id.amount_minus_button)
    void onClickAmountMinus() {
        TouchResponse.medium();

        if (amount <= 1) {
            return;
        }

        amountTextView.setText(String.valueOf(--amount));
    }
}
