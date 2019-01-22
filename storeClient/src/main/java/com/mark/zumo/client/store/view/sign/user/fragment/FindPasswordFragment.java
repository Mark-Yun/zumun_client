/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.user.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 12. 11.
 */
public class FindPasswordFragment extends Fragment implements BackPressedInterceptor {

    private Runnable onBackPressedAction;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_user_find_my_password, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    @OnClick(R.id.back)
    public void onClickedBackButton() {
        onBackPressedAction.run();
    }

    public FindPasswordFragment doOnBackPressured(Runnable runnable) {
        onBackPressedAction = runnable;
        return this;
    }
}
