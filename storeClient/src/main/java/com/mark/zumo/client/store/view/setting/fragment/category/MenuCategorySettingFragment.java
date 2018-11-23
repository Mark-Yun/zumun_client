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

package com.mark.zumo.client.store.view.setting.fragment.category;

import android.app.AlertDialog;
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

import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 6. 26.
 */
public class MenuCategorySettingFragment extends Fragment implements OnStartDragListener {

    private static final String TAG = "MenuCategorySettingFragment";

    @BindView(R.id.category_recycler_view) RecyclerView categoryRecyclerView;
    @BindView(R.id.menu_recycler_view) RecyclerView menuRecyclerView;
    @BindView(R.id.button_box) LinearLayout buttonBox;
    @BindView(R.id.mode_description_icon) AppCompatImageView modeDescriptionIcon;
    @BindView(R.id.mode_description_text) AppCompatTextView modeDescriptionText;
    @BindView(R.id.mode_confirm_button) AppCompatButton modeConfirmButton;
    @BindView(R.id.mode_description_layout) ConstraintLayout modeDescriptionLayout;

    private MenuSettingViewModel menuSettingViewModel;
    private ItemTouchHelper itemTouchHelper;
    private MenuCategoryAdapter menuCategoryAdapter;

    private List<MenuCategory> selectedMenuCategoryList;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuSettingViewModel = ViewModelProviders.of(this).get(MenuSettingViewModel.class);
        selectedMenuCategoryList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_menu_category, container, false);
        ButterKnife.bind(this, view);

        inflateRecyclerView();
        return view;
    }

    private void startAnyMode() {
        selectedMenuCategoryList.clear();
        updateMenuBar();
    }

    private void updateMenuBar() {
        MenuCategorySettingModeSelectee.SettingMode settingMode = menuCategoryAdapter.getMode();
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

    private void inflateRecyclerView() {
        categoryRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        categoryRecyclerView.setLayoutManager(layoutManager);

        menuCategoryAdapter = new MenuCategoryAdapter(getOnSelectCategoryListener(), menuSettingViewModel, this, this);
        categoryRecyclerView.setAdapter(menuCategoryAdapter);

        ItemTouchHelper.Callback callback = new CategorySettingTouchHelperCallback(menuCategoryAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(categoryRecyclerView);

        loadMenuCategoryList();
    }

    private void loadMenuCategoryList() {
        menuSettingViewModel.getCombinedMenuCategoryList().observe(this, menuCategoryAdapter::setMenuCategoryList);
    }

    @NonNull
    private MenuCategoryAdapter.OnSelectCategoryListener getOnSelectCategoryListener() {
        return new MenuCategoryAdapter.OnSelectCategoryListener() {
            @Override
            public void onSelectMenuCategory(final MenuCategory menuCategory) {
            }

            @Override
            public void onClickMenuCategoryOption(final MenuCategory menuCategory) {
                menuRecyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                menuRecyclerView.setLayoutManager(layoutManager);

                MenuCategoryMenuListAdapter adapter = new MenuCategoryMenuListAdapter(MenuCategorySettingFragment.this, menuSettingViewModel);
                menuRecyclerView.setAdapter(adapter);
                adapter.setMenuList(menuCategory.menuList);
            }

            @Override
            public void onModifyMenuCategory(final MenuCategory menuCategory, final MenuCategoryUpdateListener listener) {
                final AppCompatEditText editText = new AppCompatEditText(getContext());
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.menu_category_setting_update_category_dialog_title)
                        .setMessage(R.string.menu_category_setting_update_category_dialog_message)
                        .setView(editText)
                        .setCancelable(true)
                        .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                        .setPositiveButton(R.string.button_text_apply, (dialog, which) -> {
                            menuSettingViewModel.updateCategoryName(menuCategory, editText.getText().toString())
                                    .observe(MenuCategorySettingFragment.this, listener::onMenuCategoryUpdated);
                            dialog.dismiss();
                        })
                        .create()
                        .show();
            }

            @Override
            public void onSelectMenuCategory(final MenuCategory menuCategory, final boolean isChecked) {
                Log.d(TAG, "onSelectMenuCategory: menuCategory=" + menuCategory + " isChecked=" + isChecked);
                if (isChecked) {
                    selectedMenuCategoryList.add(menuCategory);
                } else {
                    selectedMenuCategoryList.remove(menuCategory);
                }
            }

            @Override
            public void onReorderMenuCategory(final List<MenuCategory> menuCategoryList) {

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
        Context context = getContext();
        AppCompatEditText editText = new AppCompatEditText(context);

        new AlertDialog.Builder(context)
                .setTitle(R.string.menu_category_setting_add_new_category_dialog_title)
                .setMessage(R.string.menu_category_setting_add_new_category_dialog_message)
                .setView(editText)
                .setCancelable(true)
                .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.button_text_apply, (dialog, which) -> {
                    menuSettingViewModel.createCategory(editText.getText().toString(), -1)
                            .observe(this, menuCategoryAdapter::onCreateMenuCategory);
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @OnClick(R.id.reorder)
    void reorderClick() {
        menuCategoryAdapter.setMode(MenuCategorySettingModeSelectee.SettingMode.REORDER_MODE);
        startAnyMode();
    }

    @OnClick(R.id.edit)
    void editClick() {
        menuCategoryAdapter.setMode(MenuCategorySettingModeSelectee.SettingMode.EDIT_MODE);
        startAnyMode();
    }

    @OnClick(R.id.delete)
    void deleteClick() {
        menuCategoryAdapter.setMode(MenuCategorySettingModeSelectee.SettingMode.DELETE_MODE);
        startAnyMode();
    }

    @OnClick(R.id.mode_confirm_button)
    void onClickModeConfirmButton() {
        Log.d(TAG, "onClickModeConfirmButton: selectedMenuCategoryList=" + selectedMenuCategoryList);
        if (!selectedMenuCategoryList.isEmpty()) {
            switch (menuCategoryAdapter.getMode()) {
                case DELETE_MODE:
                    menuSettingViewModel.removeCategory(new ArrayList<>(selectedMenuCategoryList)).observe(this, menuCategoryAdapter::onRemoveCategory);
                    break;
            }
        }
        menuCategoryAdapter.setMode(MenuCategorySettingModeSelectee.SettingMode.NONE);
        startAnyMode();
    }
}
