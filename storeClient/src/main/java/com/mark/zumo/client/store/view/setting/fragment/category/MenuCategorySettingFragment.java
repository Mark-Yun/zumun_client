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
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MenuSettingViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 6. 26.
 */
public class MenuCategorySettingFragment extends Fragment implements OnStartDragListener {

    @BindView(R.id.category_recycler_view) RecyclerView categoryRecyclerView;
    @BindView(R.id.menu_recycler_view) RecyclerView menuRecyclerView;

    private MenuSettingViewModel menuSettingViewModel;
    private ItemTouchHelper itemTouchHelper;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuSettingViewModel = ViewModelProviders.of(this).get(MenuSettingViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_category_setting, container, false);
        ButterKnife.bind(this, view);

        inflateRecyclerView();
        return view;
    }

    private void inflateRecyclerView() {
        categoryRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        categoryRecyclerView.setLayoutManager(layoutManager);

        MenuCategoryAdapter adapter = new MenuCategoryAdapter(this::onSelectCategory, menuSettingViewModel, this, this);
        categoryRecyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new CategorySettingTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(categoryRecyclerView);
        menuSettingViewModel.getCombinedMenuCategoryList().observe(this, adapter::setMenuCategoryList);
    }

    private void onSelectCategory(MenuCategory menuCategory) {
        menuRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        menuRecyclerView.setLayoutManager(layoutManager);

        CategoryMenuListAdapter adapter = new CategoryMenuListAdapter(this, menuSettingViewModel);
        menuRecyclerView.setAdapter(adapter);
        adapter.setMenuList(menuCategory.menuList);
    }

    @Override
    public void onStartDrag(final RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @OnClick(R.id.create_new_category)
    void onClickCreateCategory() {
        Context context = getContext();
        AppCompatEditText editText = new AppCompatEditText(context);

        new AlertDialog.Builder(context)
                .setTitle(R.string.menu_category_setting_add_new_category_dialog_title)
                .setMessage(R.string.menu_category_setting_add_new_category_dialog_message)
                .setView(editText)
                .setCancelable(true)
                .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.button_text_apply, (dialog, which) -> {
//                    menuSettingViewModel.createCategory(editText.getText().toString(), seqNum + 1)
//                            .observe(this, this::onCreateMenuCategory);
                    dialog.dismiss();
                })
                .create()
                .show();
    }
}
