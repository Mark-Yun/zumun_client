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

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.option.optionlist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
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

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOptionCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.setting.SettingModeSelectee;
import com.mark.zumo.client.store.view.util.draghelper.reorder.DragNDropReorderHelperCallback;
import com.mark.zumo.client.store.view.util.draghelper.reorder.OnStartDragListener;
import com.mark.zumo.client.store.viewmodel.MenuOptionSettingViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 6. 25.
 */
public class MenuOptionSettingOptionListFragment extends Fragment implements OnStartDragListener {

    private static final String TAG = "MenuOptionSettingFragment";

    @BindView(R.id.menu_option_setting_header) ConstraintLayout menuOptionSettingHeader;
    @BindView(R.id.menu_option_recycler_view) RecyclerView menuOptionRecyclerView;
    @BindView(R.id.create_new_option) ConstraintLayout createNewOption;
    @BindView(R.id.button_box) LinearLayout buttonBox;
    @BindView(R.id.mode_description_icon) AppCompatImageView modeDescriptionIcon;
    @BindView(R.id.mode_description_text) AppCompatTextView modeDescriptionText;
    @BindView(R.id.mode_confirm_button) AppCompatButton modeConfirmButton;
    @BindView(R.id.mode_description_layout) ConstraintLayout modeDescriptionLayout;

    private MenuOptionSettingViewModel menuOptionSettingViewModel;
    private MenuOptionSettingOptionListAdapter menuOptionSettingOptionListAdapter;

    private List<MenuOption> selectedMenuOptionList;
    private List<MenuOptionCategory> selectedMenuOptionCategoryList;
    private SelectMenuOptionCategoryLister selectMenuOptionCategoryListener;

    private ItemTouchHelper itemTouchHelper;

    public void setSelectMenuOptionCategoryListener(final SelectMenuOptionCategoryLister selectMenuOptionCategoryListener) {
        this.selectMenuOptionCategoryListener = selectMenuOptionCategoryListener;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuOptionSettingViewModel = ViewModelProviders.of(this).get(MenuOptionSettingViewModel.class);
        selectedMenuOptionList = new ArrayList<>();
        selectedMenuOptionCategoryList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu_option_option_list, container, false);
        ButterKnife.bind(this, view);

        inflateMenuOptionList();
        return view;
    }

    private void inflateMenuOptionList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        menuOptionRecyclerView.setLayoutManager(layoutManager);
        menuOptionRecyclerView.setHasFixedSize(true);
        menuOptionRecyclerView.setNestedScrollingEnabled(false);

        menuOptionSettingOptionListAdapter = new MenuOptionSettingOptionListAdapter(getSelectMenuOptionListener(), this);
        menuOptionRecyclerView.setAdapter(menuOptionSettingOptionListAdapter);

        ItemTouchHelper.Callback callback = new DragNDropReorderHelperCallback(menuOptionSettingOptionListAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(menuOptionRecyclerView);

        menuOptionSettingViewModel.getCombinedMenuOptionCategoryList().observe(this, menuOptionSettingOptionListAdapter::setMenuOptionCategoryList);
    }

    @NonNull
    private MenuOptionSettingOptionListAdapter.MenuOptionSelectListener getSelectMenuOptionListener() {
        return new MenuOptionSettingOptionListAdapter.MenuOptionSelectListener() {
            @Override
            public void onClickMenuOptionCategory(final MenuOptionCategory menuOptionCategory) {
                MenuOptionSettingOptionListFragment.this.onClickMenuCategoryOption(menuOptionCategory);
            }

            @Override
            public void onSelectMenuOptionCategory(final MenuOptionCategory menuOptionCategory, final boolean isChecked) {
                if (isChecked) {
                    selectedMenuOptionCategoryList.add(menuOptionCategory);
                } else {
                    selectedMenuOptionCategoryList.remove(menuOptionCategory);
                }
            }

            @Override
            public void onModifyMenuOptionCategory(final MenuOptionCategory menuOptionCategory) {

            }

            @Override
            public void onReorderMenuOptionCategory(final List<MenuOptionCategory> menuOptionCategoryList) {
                selectedMenuOptionCategoryList.clear();
                selectedMenuOptionCategoryList.addAll(menuOptionCategoryList);
            }

            @Override
            public void onClickMenuOption(final MenuOption menuOption) {

            }

            @Override
            public void onModifyMenuOption(final MenuOption menuOption) {

            }

            @Override
            public void onSelectMenuOption(final MenuOption menuOption, final boolean isChecked) {
                if (isChecked) {
                    selectedMenuOptionList.add(menuOption);
                } else {
                    selectedMenuOptionList.remove(menuOption);
                }
            }

            @Override
            public void onReorderMenuOption(final List<MenuOption> menuOptionList) {

            }
        };
    }

    private void onClickMenuCategoryOption(MenuOptionCategory menuOptionCategory) {
        Log.d(TAG, "onClickMenuCategoryOption: name=" + menuOptionCategory.name);
        selectMenuOptionCategoryListener.onClickMenuOptionCategory(menuOptionCategory);
    }

    private void startAnyMode() {
        selectedMenuOptionList.clear();
        updateMenuBar();
    }

    private void updateMenuBar() {
        SettingModeSelectee.SettingMode settingMode = menuOptionSettingOptionListAdapter.getMode();
        boolean isInAnyMode = !settingMode.isNone();
        buttonBox.setVisibility(!isInAnyMode ? View.VISIBLE : View.GONE);
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

    @Override
    public void onResume() {
        super.onResume();
        startAnyMode();
    }

    @OnClick(R.id.add)
    void addClick() {
        Context context = getContext();
        AppCompatEditText editText = new AppCompatEditText(context);

        new android.app.AlertDialog.Builder(context)
                .setTitle(R.string.menu_category_setting_add_new_category_dialog_title)
                .setMessage(R.string.menu_category_setting_add_new_category_dialog_message)
                .setView(editText)
                .setCancelable(true)
                .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.button_text_apply, (dialog, which) -> {
                    menuOptionSettingViewModel.createMenuOptionCategory(editText.getText().toString())
                            .observe(this, menuOptionSettingOptionListAdapter::onCreateMenuCategory);
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @OnClick(R.id.reorder)
    void reorderClick() {
        menuOptionSettingOptionListAdapter.setMode(SettingModeSelectee.SettingMode.REORDER_MODE);
        startAnyMode();
    }

    @OnClick(R.id.edit)
    void editClick() {
        menuOptionSettingOptionListAdapter.setMode(SettingModeSelectee.SettingMode.EDIT_MODE);
        startAnyMode();
    }

    @OnClick(R.id.delete)
    void deleteClick() {
        menuOptionSettingOptionListAdapter.setMode(SettingModeSelectee.SettingMode.DELETE_MODE);
        startAnyMode();
    }

    @OnClick(R.id.mode_confirm_button)
    void onClickModeConfirmButton() {

        Log.d(TAG, "onClickModeConfirmButton: selectedMenuOptionCategoryList=" + selectedMenuOptionCategoryList);
        if (!selectedMenuOptionCategoryList.isEmpty()) {
            switch (menuOptionSettingOptionListAdapter.getMode()) {
                case DELETE_MODE:
                    menuOptionSettingViewModel.removeMenuOptionCategory(new ArrayList<>(selectedMenuOptionCategoryList))
                            .observe(this, menuOptionSettingOptionListAdapter::onMenuOptionCategoryListRemoved);
                    break;
                case REORDER_MODE:
                    menuOptionSettingViewModel.reorderMenuOptionCategory(new ArrayList<>(selectedMenuOptionCategoryList))
                            .observe(this, menuOptionSettingOptionListAdapter::setMenuOptionCategoryList);
                    break;
            }
        }

        Log.d(TAG, "onClickModeConfirmButton: selectedMenuOptionList=" + selectedMenuOptionList);
        if (!selectedMenuOptionList.isEmpty()) {
            switch (menuOptionSettingOptionListAdapter.getMode()) {
                case DELETE_MODE:
                    menuOptionSettingViewModel.removeMenuOption(new ArrayList<>(selectedMenuOptionList))
                            .observe(this, menuOptionSettingOptionListAdapter::onMenuOptionListRemoved);
                    break;
                case REORDER_MODE:
                    menuOptionSettingViewModel.reorderMenuOption(new ArrayList<>(selectedMenuOptionList))
                            .observe(this, menuOptionSettingOptionListAdapter::onMenuOptionListUpdated);
                    break;
            }
        }

        menuOptionSettingOptionListAdapter.setMode(SettingModeSelectee.SettingMode.NONE);
        startAnyMode();
    }

    @Override
    public void onStartDrag(final RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @FunctionalInterface
    public interface SelectMenuOptionCategoryLister {
        void onClickMenuOptionCategory(final MenuOptionCategory menuOptionCategory);
    }
}
