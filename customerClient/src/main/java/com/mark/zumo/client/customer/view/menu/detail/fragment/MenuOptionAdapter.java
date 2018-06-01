/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu.detail.fragment;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.customer.viewmodel.MenuDetailViewModel;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mark on 18. 5. 24.
 */
public class MenuOptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "MenuOptionAdapter";

    private MenuDetailViewModel menuDetailViewModel;
    private LifecycleOwner lifecycleOwner;

    private Map<String, List<MenuOption>> optionMap;

    MenuOptionAdapter(LifecycleOwner lifecycleOwner, MenuDetailViewModel menuDetailViewModel) {
        this.menuDetailViewModel = menuDetailViewModel;
        this.lifecycleOwner = lifecycleOwner;

        optionMap = new LinkedHashMap<>();
    }

    void setOptionMap(final Map<String, List<MenuOption>> optionMap) {
        this.optionMap = optionMap;
        Log.d(TAG, "setOptionMap: " + optionMap.keySet().size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return ViewHolderUtils.inflate(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case ViewHolderUtils.SINGLE_SELECT_TYPE:
                if (holder instanceof SingleSelectViewHolder)
                    ViewHolderUtils.inject((SingleSelectViewHolder) holder, getKey(position), getMenuOptionList(position).get(0), menuDetailViewModel, lifecycleOwner);
                break;

            case ViewHolderUtils.MULTI_SELECT_TYPE:
                if (holder instanceof MultiSelectViewHolder)
                    ViewHolderUtils.inject((MultiSelectViewHolder) holder, getKey(position), getMenuOptionList(position), menuDetailViewModel, lifecycleOwner);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return optionMap.keySet().size();
    }

    @Override
    public int getItemViewType(final int position) {
        List<MenuOption> menuOption = getMenuOptionList(position);
        if (menuOption.size() == 1) {
            return ViewHolderUtils.SINGLE_SELECT_TYPE;
        } else {
            return ViewHolderUtils.MULTI_SELECT_TYPE;
        }
    }

    private List<MenuOption> getMenuOptionList(final int position) {
        String key = getKey(position);
        return optionMap.get(key);
    }

    private String getKey(final int position) {
        List<String> keyList = Arrays.asList(optionMap.keySet().toArray(new String[]{}));
        return keyList.get(position);
    }
}
