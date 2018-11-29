/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.store.view.setting.fragment.category;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.SettingModeSelectee;
import com.mark.zumo.client.store.view.setting.fragment.menu.selector.MenuSelectorDialogFragment;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 6. 26.
 */
public class MenuCategorySettingMenuListFragment extends Fragment implements OnStartDragListener {

    private static final String TAG = "MenuCategorySettingCategoryListFragment";

    @BindView(R.id.menu_recycler_view) RecyclerView menuRecyclerView;
    @BindView(R.id.button_box) LinearLayout buttonBox;
    @BindView(R.id.mode_description_icon) AppCompatImageView modeDescriptionIcon;
    @BindView(R.id.mode_description_text) AppCompatTextView modeDescriptionText;
    @BindView(R.id.mode_confirm_button) AppCompatButton modeConfirmButton;
    @BindView(R.id.mode_description_layout) ConstraintLayout modeDescriptionLayout;

    private MenuSettingViewModel menuSettingViewModel;
    private ItemTouchHelper itemTouchHelper;
    private MenuCategorySettingMenuListAdapter menuListAdapter;

    private List<MenuCategory> selectedMenuCategoryList;
    private MenuCategory selectedMenuCategory;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuSettingViewModel = ViewModelProviders.of(this).get(MenuSettingViewModel.class);
        selectedMenuCategoryList = new ArrayList<>();
    }

    public void onSelectMenuCategory(final MenuCategory menuCategory) {
        selectedMenuCategory = menuCategory;
        menuListAdapter.setMenuList(new ArrayList<>(selectedMenuCategory.menuList));

        updateButtonBoxVisibility();
    }

    private void updateButtonBoxVisibility() {
        boolean canShowButtonBox = selectedMenuCategory != null && !selectedMenuCategory.uuid.isEmpty();
        buttonBox.setVisibility(canShowButtonBox ? View.VISIBLE : View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu_category_menu_list, container, false);
        ButterKnife.bind(this, view);

        updateButtonBoxVisibility();
        inflateRecyclerView();
        return view;
    }

    private void startAnyMode() {
        selectedMenuCategoryList.clear();
        updateMenuBar();
    }

    private void updateMenuBar() {
        SettingModeSelectee.SettingMode settingMode = menuListAdapter.getMode();
        boolean isInAnyMode = !settingMode.isNone();
        buttonBox.setVisibility(!isInAnyMode ? View.VISIBLE : View.GONE);
        updateButtonBoxVisibility();
        modeDescriptionLayout.setVisibility(isInAnyMode ? View.VISIBLE : View.GONE);
        if (isInAnyMode) {
            int iconResId;
            int textResId;
            switch (settingMode) {
                case REORDER_MODE:
                    iconResId = R.drawable.ic_swap_vert_grey_700_48dp;
                    textResId = R.string.option_setting_mode_description_reorder;
                    break;
                case EDIT_MODE:
                    iconResId = R.drawable.ic_edit_grey_700_48dp;
                    textResId = R.string.option_setting_mode_description_edit;
                    break;
                case DELETE_MODE:
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

    private void inflateRecyclerView() {
        menuRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        menuRecyclerView.setLayoutManager(layoutManager);

        menuListAdapter = new MenuCategorySettingMenuListAdapter(getListener());
        menuRecyclerView.setAdapter(menuListAdapter);

        ItemTouchHelper.Callback callback = new CategorySettingTouchHelperCallback(menuListAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(menuRecyclerView);
    }

    @NonNull
    private MenuCategorySettingMenuListAdapter.SelectMenuListener getListener() {
        return new MenuCategorySettingMenuListAdapter.SelectMenuListener() {
            @Override
            public void onModifyMenuList(final List<Menu> menuList) {

            }

            @Override
            public void onDeleteMenuList(final List<Menu> menuList) {

            }

            @Override
            public void onSelectMenuList(final List<Menu> menuList) {

            }
        };
    }

    @Override
    public void onStartDrag(final RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onResume() {
        super.onResume();
        startAnyMode();
    }

    @OnClick(R.id.add)
    void addClick() {
        String titleText = getString(R.string.menu_selector_title_to_select_menus_to_add_into_category, selectedMenuCategory.name);

        MenuSelectorDialogFragment.newInstance()
                .except(menuListAdapter.getMenuList())
                .setTextTitle(titleText)
                .onSelect(this::onMenuListSelectedAdditionally)
                .show(getFragmentManager(), this.getClass().getSimpleName());
    }

    private void onMenuListSelectedAdditionally(List<Menu> menuList) {
        menuSettingViewModel.createMenuDetailListAsMenuList(selectedMenuCategory, menuList).observe(this, menuListAdapter::onMenuDetailCreated);
    }

    @OnClick(R.id.reorder)
    void reorderClick() {
        menuListAdapter.setMode(SettingModeSelectee.SettingMode.REORDER_MODE);
        startAnyMode();
    }

    @OnClick(R.id.edit)
    void editClick() {
        menuListAdapter.setMode(SettingModeSelectee.SettingMode.EDIT_MODE);
        startAnyMode();
    }

    @OnClick(R.id.delete)
    void deleteClick() {
        menuListAdapter.setMode(SettingModeSelectee.SettingMode.DELETE_MODE);
        startAnyMode();
    }

    @OnClick(R.id.mode_confirm_button)
    void onClickModeConfirmButton() {
        Log.d(TAG, "onClickModeConfirmButton: selectedMenuCategoryList=" + selectedMenuCategoryList);
        if (!selectedMenuCategoryList.isEmpty()) {
            switch (menuListAdapter.getMode()) {
                case DELETE_MODE:
                    break;
            }
        }
        menuListAdapter.setMode(SettingModeSelectee.SettingMode.NONE);
        startAnyMode();
    }
}
