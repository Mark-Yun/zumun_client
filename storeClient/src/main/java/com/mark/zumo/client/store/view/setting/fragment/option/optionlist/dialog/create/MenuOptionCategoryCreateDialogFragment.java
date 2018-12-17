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

package com.mark.zumo.client.store.view.setting.fragment.option.optionlist.dialog.create;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOptionCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuOptionSettingViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 12. 5.
 */
public class MenuOptionCategoryCreateDialogFragment extends DialogFragment {

    @BindView(R.id.title) AppCompatTextView title;
    @BindView(R.id.option_name_input_text) TextInputEditText optionNameInputText;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.ok) AppCompatTextView ok;
    @BindView(R.id.cancel) AppCompatTextView cancel;

    private MenuOptionSettingViewModel menuOptionSettingViewModel;
    private MenuOptionCategoryCreateListener menuOptionCategoryCreateListener;

    private MenuOptionAdapter menuOptionAdapter;

    public static MenuOptionCategoryCreateDialogFragment newInstance() {
        Bundle args = new Bundle();

        MenuOptionCategoryCreateDialogFragment fragment = new MenuOptionCategoryCreateDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MenuOptionCategoryCreateDialogFragment onCreateMenuOptionCategory(final MenuOptionCategoryCreateListener menuOptionCategoryCreateListener) {
        this.menuOptionCategoryCreateListener = menuOptionCategoryCreateListener;
        return this;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuOptionSettingViewModel = ViewModelProviders.of(this).get(MenuOptionSettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_create_menu_option, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
        title.setText("Create Menu Option");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);

        menuOptionAdapter = new MenuOptionAdapter();
        recyclerView.setAdapter(menuOptionAdapter);
    }

    private void onCreateMenuOptionCategoryInternal(final MenuOptionCategory menuOptionCategory) {
        if (menuOptionCategoryCreateListener != null) {
            menuOptionCategoryCreateListener.onCreateMenuOptionCategory(menuOptionCategory);
        }
        dismiss();
    }

    @OnClick(R.id.ok)
    void onOkClick() {
        String menuOptionCategoryName = optionNameInputText.getText().toString();
        List<MenuOption> menuOptionList = menuOptionAdapter.getMenuOptionList();
        menuOptionSettingViewModel.createMenuOptionCategory(menuOptionCategoryName, new ArrayList<>(menuOptionList))
                .observe(this, this::onCreateMenuOptionCategoryInternal);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @OnClick(R.id.cancel)
    void onCancelClick() {
        dismiss();
    }

    @FunctionalInterface
    public interface MenuOptionCategoryCreateListener {
        void onCreateMenuOptionCategory(MenuOptionCategory menuOptionCategory);
    }
}
