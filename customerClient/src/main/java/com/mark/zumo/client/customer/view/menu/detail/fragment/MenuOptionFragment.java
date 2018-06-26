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
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.view.TouchResponse;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.menu.detail.MenuDetailActivity;
import com.mark.zumo.client.customer.viewmodel.MenuDetailViewModel;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 5. 24.
 */
public class MenuOptionFragment extends Fragment {

    @BindView(R.id.menu_option_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.personal_option) ConstraintLayout personalOption;
    @BindView(R.id.amount) AppCompatTextView amount;

    private String menuUuid;
    private MenuDetailViewModel menuDetailViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuDetailViewModel = ViewModelProviders.of(getActivity()).get(MenuDetailViewModel.class);
        menuUuid = getArguments().getString(MenuDetailActivity.KEY_MENU_UUID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_option, container, false);
        ButterKnife.bind(this, view);
        inflateAmount();
        inflateRecyclerView();
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
        String initialAmount = menuDetailViewModel.getMenuAmount();
        amount.setText(initialAmount);
    }

    @OnClick(R.id.amount_plus_button)
    void onClickAmountPlus() {
        TouchResponse.medium();
        String increaseAmount = menuDetailViewModel.increaseAmount();
        amount.setText(increaseAmount);
    }

    @OnClick(R.id.amount_minus_button)
    void onClickAmountMinus() {
        TouchResponse.medium();
        String decreaseAmount = menuDetailViewModel.decreaseAmount();
        amount.setText(decreaseAmount);
    }

    @OnClick(R.id.personal_option)
    void onClickPersonalOption() {
        TouchResponse.small();
        Toast.makeText(getActivity(), "IMPL ME", Toast.LENGTH_SHORT).show();
    }
}
