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

package com.mark.zumo.client.store.view.main.fragment.storeselect;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.user.store.StoreUserContract;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 12. 27.
 */
public class StoreSelectFragment extends Fragment {

    private static final String TAG = "StoreSelectFragment";

    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tab_indicator) TabLayout tabIndicator;
    @BindView(R.id.store_select_layout) ConstraintLayout storeSelectLayout;
    @BindView(R.id.non_store_layout) ConstraintLayout nonStoreLayout;
    @BindView(R.id.move_store_registration_up) AppCompatButton moveStoreRegistrationUp;

    private MainViewModel mainViewModel;

    private StoreSelectListener listener;
    private Runnable storeRegistrationAction;
    private StoreSelectPagerAdapter storeSelectPagerAdapter;

    public StoreSelectFragment onSelectStore(final StoreSelectListener listener) {
        this.listener = listener;
        return this;
    }

    public StoreSelectFragment onClickStoreRegistration(Runnable storeRegistrationAction) {
        this.storeRegistrationAction = storeRegistrationAction;
        return this;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_select, container, false);
        ButterKnife.bind(this, view);

        tabIndicator.setupWithViewPager(viewPager);
        storeSelectPagerAdapter = new StoreSelectPagerAdapter(this, listener);
        viewPager.setAdapter(storeSelectPagerAdapter);
        mainViewModel.getStoreUserContract().observe(this, this::onLoadedStoreContractList);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabIndicator));

        return view;
    }

    private void onLoadedStoreContractList(List<StoreUserContract> storeUserContractList) {
        boolean hasStoreUserContract = storeUserContractList != null && storeUserContractList.size() > 0;

        Log.d(TAG, "onLoadedStoreContractList: hasStoreUserContract=" + hasStoreUserContract);

        storeSelectLayout.setVisibility(hasStoreUserContract ? View.VISIBLE : View.GONE);
        nonStoreLayout.setVisibility(hasStoreUserContract ? View.GONE : View.VISIBLE);

        if (!hasStoreUserContract) {
            Log.i(TAG, "onLoadedStoreContractList: hasStoreUserContract=" + hasStoreUserContract);
            return;
        }

        if (storeUserContractList.size() > 1) {
            storeSelectPagerAdapter.setStoreUserContractList(storeUserContractList);
        } else {
            listener.onSelectStore(storeUserContractList.get(0).storeUuid);
        }

    }

    @OnClick(R.id.move_store_registration_up)
    public void onViewClicked() {
        storeRegistrationAction.run();
    }

    public interface StoreSelectListener extends StoreSelectPagerAdapter.StoreSelectListener {
    }
}
