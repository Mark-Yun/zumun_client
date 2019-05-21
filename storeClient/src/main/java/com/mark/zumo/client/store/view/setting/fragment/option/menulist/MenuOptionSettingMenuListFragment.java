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

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.store.view.setting.fragment.option.menulist;

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

import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.core.database.entity.MenuOptionCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.SettingModeSelectee;
import com.mark.zumo.client.store.view.setting.fragment.menu.selector.MenuSelectorDialogFragment;
import com.mark.zumo.client.store.view.util.draghelper.reorder.DragNDropReorderHelperCallback;
import com.mark.zumo.client.store.view.util.draghelper.reorder.OnStartDragListener;
import com.mark.zumo.client.store.viewmodel.MenuOptionSettingViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 6. 26.
 */
public class MenuOptionSettingMenuListFragment extends Fragment implements OnStartDragListener {

    private static final String TAG = "MenuOptionSettingMenuListFragment";

    @BindView(R.id.menu_recycler_view) RecyclerView menuRecyclerView;
    @BindView(R.id.button_box) LinearLayout buttonBox;
    @BindView(R.id.mode_description_icon) AppCompatImageView modeDescriptionIcon;
    @BindView(R.id.mode_description_text) AppCompatTextView modeDescriptionText;
    @BindView(R.id.mode_confirm_button) AppCompatButton modeConfirmButton;
    @BindView(R.id.mode_description_layout) ConstraintLayout modeDescriptionLayout;

    private MenuOptionSettingViewModel menuOptionSettingViewModel;
    private ItemTouchHelper itemTouchHelper;
    private MenuOptionSettingMenuListAdapter menuListAdapter;

    private List<Menu> selectedMenuList;
    private MenuOptionCategory selectedMenuOptionCategory;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuOptionSettingViewModel = ViewModelProviders.of(this).get(MenuOptionSettingViewModel.class);
        selectedMenuList = new ArrayList<>();
    }

    public void onSelectMenuOptionCategory(final MenuOptionCategory menuOptionCategory) {
        selectedMenuOptionCategory = menuOptionCategory;
        menuListAdapter.setMenuList(selectedMenuOptionCategory.menuList);
        updateButtonBoxVisibility();
    }

    private void updateButtonBoxVisibility() {
        SettingModeSelectee.SettingMode settingMode = menuListAdapter.getMode();
        boolean isInAnyMode = !settingMode.isNone();

        boolean canShowButtonBox = selectedMenuOptionCategory != null && !selectedMenuOptionCategory.uuid.isEmpty()
                && !isInAnyMode;
        buttonBox.setVisibility(canShowButtonBox ? View.VISIBLE : View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu_option_menu_list, container, false);
        ButterKnife.bind(this, view);

        inflateRecyclerView();
        updateButtonBoxVisibility();
        return view;
    }

    private void startAnyMode() {
        selectedMenuList.clear();
        updateMenuBar();
    }

    private void updateMenuBar() {
        SettingModeSelectee.SettingMode settingMode = menuListAdapter.getMode();
        boolean isInAnyMode = !settingMode.isNone();
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

        menuListAdapter = new MenuOptionSettingMenuListAdapter(getListener(), this);
        menuRecyclerView.setAdapter(menuListAdapter);

        ItemTouchHelper.Callback callback = new DragNDropReorderHelperCallback(menuListAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(menuRecyclerView);
    }

    @NonNull
    private MenuOptionSettingMenuListAdapter.SelectMenuListener getListener() {
        return new MenuOptionSettingMenuListAdapter.SelectMenuListener() {
            @Override
            public void onReorderMenuList(final List<Menu> menuUuidList) {
                selectedMenuList.clear();
                selectedMenuList.addAll(menuUuidList);
            }

            @Override
            public void onModifyMenu(final Menu menu) {

            }

            @Override
            public void onSelectMenuList(final Menu menu, final boolean isChecked) {
                if (isChecked) {
                    selectedMenuList.add(menu);
                } else {
                    selectedMenuList.remove(menu);
                }
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
        String titleText = getString(R.string.menu_selector_title_to_select_menus_to_add_into_category, selectedMenuOptionCategory.name);

        MenuSelectorDialogFragment.newInstance()
                .except(menuListAdapter.getMenuList())
                .setTextTitle(titleText)
                .onSelect(this::onMenuListSelectedAdditionally)
                .show(getFragmentManager(), this.getClass().getSimpleName());
    }

    private void onMenuListSelectedAdditionally(List<Menu> menuList) {
        menuOptionSettingViewModel.createMenuOptionDetailList(selectedMenuOptionCategory.uuid, new ArrayList<>(menuList))
                .observe(this, menuListAdapter::onMenuDetailCreated);
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
        Log.d(TAG, "onClickModeConfirmButton: selectedMenuList=" + selectedMenuList);
        if (!selectedMenuList.isEmpty()) {
            switch (menuListAdapter.getMode()) {
                case DELETE_MODE:
                    menuOptionSettingViewModel.removeMenuOptionDetail(selectedMenuOptionCategory, new ArrayList<>(selectedMenuList))
                            .observe(this, menuListAdapter::onRemoveMenuList);
                    break;
            }
        }
        menuListAdapter.setMode(SettingModeSelectee.SettingMode.NONE);
        startAnyMode();
    }
}
