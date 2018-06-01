/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu.detail.fragment;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.MenuDetailViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.exceptions.OnErrorNotImplementedException;

/**
 * Created by mark on 18. 5. 25.
 */
final class ViewHolderUtils {

    static final int SINGLE_SELECT_TYPE = 0;
    static final int MULTI_SELECT_TYPE = 1;

    private static final int SINGLE_SELECT_RES = R.layout.card_view_menu_option_single_select;
    private static final int MULTI_SELECT_RES = R.layout.card_view_menu_option_multi_select;

    private ViewHolderUtils() {
        /*Empty Body*/
    }

    static RecyclerView.ViewHolder inflate(ViewGroup parent, int viewType) {
        int resId;
        switch (viewType) {
            case SINGLE_SELECT_TYPE:
                resId = SINGLE_SELECT_RES;
                View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                return new SingleSelectViewHolder(view);

            case MULTI_SELECT_TYPE:
                resId = MULTI_SELECT_RES;
                view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                return new MultiSelectViewHolder(view);

            default:
                throw new OnErrorNotImplementedException(new Throwable());
        }
    }

    static void inject(SingleSelectViewHolder viewHolder, String key,
                       MenuOption menuOption, MenuDetailViewModel menuDetailViewModel,
                       LifecycleOwner lifecycleOwner) {
        viewHolder.name.setText(key);
        viewHolder.itemView.setOnClickListener(v -> {
            boolean checked = viewHolder.checkBox.isChecked();
            viewHolder.checkBox.setChecked(!checked);
            if (checked) {
                menuDetailViewModel.deselectMenuOption(key);
            } else {
                menuDetailViewModel.selectMenuOption(menuOption);
            }
        });
        menuDetailViewModel.getSelectedOption(key).observe(lifecycleOwner, menuOption1 -> viewHolder.checkBox.setChecked(menuOption1 != null));
    }

    @NonNull
    private static String getPriceText(final int price) {
        return price < 0 ? String.valueOf(price) : "+" + price;
    }

    static void inject(MultiSelectViewHolder viewHolder, String key,
                       List<MenuOption> menuOptionList, final MenuDetailViewModel menuDetailViewModel,
                       LifecycleOwner lifecycleOwner) {
        viewHolder.name.setText(key);

        List<String> menuStringList = new ArrayList<>();
        for (MenuOption option : menuOptionList) {
            menuStringList.add(option.value);
        }
        Context context = viewHolder.itemView.getContext();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice, menuStringList);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(key)
                .setAdapter(arrayAdapter, (dialog, which) -> menuDetailViewModel.selectMenuOption(menuOptionList.get(which)))
                .setCancelable(true)
                .setOnCancelListener(dialog -> menuDetailViewModel.deselectMenuOption(key));

        viewHolder.itemView.setOnClickListener(v -> builder.create().show());

        menuDetailViewModel.getSelectedOption(key).observe(lifecycleOwner, menuOption -> {
            String value = menuOption == null ? "" : menuOption.value;
            viewHolder.value.setText(value);
        });
    }

}
