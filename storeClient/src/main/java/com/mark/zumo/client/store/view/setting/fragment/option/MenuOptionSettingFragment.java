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

package com.mark.zumo.client.store.view.setting.fragment.option;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuOptionSettingViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 6. 25.
 */
public class MenuOptionSettingFragment extends Fragment {

    private static final String TAG = "MenuOptionSettingFragment";

    private static final int MODE_NONE = 0;
    private static final int MODE_REORDER = 1;
    private static final int MODE_EDIT = 2;
    private static final int MODE_DELETE = 3;

    @BindView(R.id.menu_option_setting_header) ConstraintLayout menuOptionSettingHeader;
    @BindView(R.id.menu_option_recycler_view) RecyclerView menuOptionRecyclerView;
    @BindView(R.id.menu_option_detail_recycler_view) RecyclerView menuOptionDetailRecyclerView;
    @BindView(R.id.create_new_option) ConstraintLayout createNewOption;
    @BindView(R.id.button_box) LinearLayout buttonBox;
    @BindView(R.id.mode_description_icon) AppCompatImageView modeDescriptionIcon;
    @BindView(R.id.mode_description_text) AppCompatTextView modeDescriptionText;
    @BindView(R.id.mode_confirm_button) AppCompatButton modeConfirmButton;
    @BindView(R.id.mode_description_layout) ConstraintLayout modeDescriptionLayout;

    private MenuOptionSettingViewModel menuOptionSettingViewModel;
    private MenuOptionMenuCategoryAdapter menuOptionMenuCategoryAdapter;
    private MenuOptionAdapter menuOptionAdapter;

    private int mode;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuOptionSettingViewModel = ViewModelProviders.of(this).get(MenuOptionSettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu_option, container, false);
        ButterKnife.bind(this, view);

        inflateMenuOptionList();
        inflateMenuOptionMenuList();
        return view;
    }

    private void inflateMenuOptionList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        menuOptionRecyclerView.setLayoutManager(layoutManager);
        menuOptionRecyclerView.setHasFixedSize(true);
        menuOptionRecyclerView.setNestedScrollingEnabled(false);

        menuOptionAdapter = new MenuOptionAdapter();
        menuOptionAdapter.setListener(this::onSelectMenuOption);
        menuOptionRecyclerView.setAdapter(menuOptionAdapter);

        menuOptionSettingViewModel.getMenuOptionList().observe(this, menuOptionAdapter::setMenuOptionList);
    }

    private void inflateMenuOptionMenuList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        menuOptionDetailRecyclerView.setLayoutManager(layoutManager);
        menuOptionDetailRecyclerView.setHasFixedSize(true);
        menuOptionDetailRecyclerView.setNestedScrollingEnabled(false);

        menuOptionMenuCategoryAdapter = new MenuOptionMenuCategoryAdapter();
        menuOptionDetailRecyclerView.setAdapter(menuOptionMenuCategoryAdapter);
    }

    private void onSelectMenuOption(List<MenuOption> menuOptionList) {
        Log.d(TAG, "onSelectMenuOption: name=" + menuOptionList.get(0).name);
        menuOptionSettingViewModel.getCategoryList().observe(this, menuOptionMenuCategoryAdapter::setMenuCategoryList);
    }

    private void updateMenuBar() {
        boolean isInAnyMode = mode != MODE_NONE;
        buttonBox.setVisibility(!isInAnyMode ? View.VISIBLE : View.GONE);
        modeDescriptionLayout.setVisibility(isInAnyMode ? View.VISIBLE : View.GONE);
        if (isInAnyMode) {
            int iconResId;
            int textResId;
            switch (mode) {
                case MODE_REORDER:
                    iconResId = R.drawable.ic_swap_vert_grey_700_48dp;
                    textResId = R.string.option_setting_mode_description_reorder;
                    break;
                case MODE_EDIT:
                    iconResId = R.drawable.ic_edit_grey_700_48dp;
                    textResId = R.string.option_setting_mode_description_edit;
                    break;
                case MODE_DELETE:
                    iconResId = R.drawable.ic_delete_forever_grey_700_48dp;
                    textResId = R.string.option_setting_mode_description_delete;
                    break;

                default:
                    throw new UnsupportedOperationException();
            }

            modeDescriptionIcon.setImageResource(iconResId);
            modeDescriptionText.setText(textResId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMenuBar();
    }

    @OnClick(R.id.add)
    void addClick() {
        new AlertDialog.Builder(getActivity())
                .setTitle("test")
                .setMessage("test")
                .create()
                .show();
    }

    @OnClick(R.id.reorder)
    void reorderClick() {
        mode = MODE_REORDER;
        menuOptionAdapter.setReorderMode(true);
        updateMenuBar();
    }

    @OnClick(R.id.edit)
    void editClick() {
        mode = MODE_EDIT;
        menuOptionAdapter.setEditMode(true);
        updateMenuBar();
    }

    @OnClick(R.id.delete)
    void deleteClick() {
        mode = MODE_DELETE;
        menuOptionAdapter.setDeleteMode(true);
        updateMenuBar();
    }

    @OnClick(R.id.mode_confirm_button)
    void onClickModeConfirmButton() {
        mode = MODE_NONE;
        menuOptionAdapter.setNoneMode();
        updateMenuBar();
    }
}
