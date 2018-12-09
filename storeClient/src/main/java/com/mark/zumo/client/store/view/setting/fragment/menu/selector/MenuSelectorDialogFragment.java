/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.menu.selector;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;

/**
 * Created by mark on 18. 11. 27.
 */
public class MenuSelectorDialogFragment extends DialogFragment {

    private final List<Menu> selectedMenuList = new ArrayList<>();
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.title) AppCompatTextView title;
    private MenuSettingViewModel menuSettingViewModel;
    private SelectMenuListListener listener;
    private List<String> menuUuidListToExcept = new ArrayList<>();
    private List<String> menuUuidListToCheck = new ArrayList<>();
    private String textTitle;

    public static MenuSelectorDialogFragment newInstance() {
        Bundle args = new Bundle();

        MenuSelectorDialogFragment fragment = new MenuSelectorDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MenuSelectorDialogFragment except(List<Menu> menuList) {
        menuUuidListToExcept.clear();
        List<String> menuUuidList = Observable.fromIterable(menuList)
                .map(menu -> menu.uuid)
                .toList()
                .blockingGet();
        menuUuidListToExcept.addAll(menuUuidList);
        return this;
    }

    public MenuSelectorDialogFragment doCheck(List<Menu> menuList) {
        menuUuidListToCheck.clear();
        List<String> menuUuidList = Observable.fromIterable(menuList)
                .map(menu -> menu.uuid)
                .toList()
                .blockingGet();
        menuUuidListToCheck.addAll(menuUuidList);
        return this;
    }

    public MenuSelectorDialogFragment onSelect(SelectMenuListListener listener) {
        this.listener = listener;
        return this;
    }

    public MenuSelectorDialogFragment setTextTitle(String textTitle) {
        this.textTitle = textTitle;
        return this;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuSettingViewModel = ViewModelProviders.of(this).get(MenuSettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dialog_menu_selector, container, false);
        ButterKnife.bind(this, view);
        title.setText(textTitle);
        inflateRecyclerView();
        return view;
    }

    private void inflateRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        MenuSelectorCategoryAdapter menuOptionMenuCategoryAdapter = new MenuSelectorCategoryAdapter(menuUuidListToExcept, menuUuidListToCheck, this::onCheckedMenu);
        recyclerView.setAdapter(menuOptionMenuCategoryAdapter);
        menuSettingViewModel.getCombinedMenuCategoryList().observe(this, menuOptionMenuCategoryAdapter::setMenuCategoryList);
    }

    private void onCheckedMenu(Menu menu, boolean isChecked) {
        if (isChecked) {
            selectedMenuList.add(menu);
        }
    }

    @OnClick(R.id.ok)
    void onClickOk() {
        this.listener.onSelectMenuList(selectedMenuList);
        getDialog().dismiss();
    }

    @OnClick(R.id.cancel)
    void onClickCancel() {
        getDialog().dismiss();
    }

    public interface SelectMenuListListener {
        void onSelectMenuList(List<Menu> menuList);
    }
}
