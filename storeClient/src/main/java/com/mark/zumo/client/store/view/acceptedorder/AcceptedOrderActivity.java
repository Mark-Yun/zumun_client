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

package com.mark.zumo.client.store.view.acceptedorder;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.mark.zumo.client.core.app.BaseActivity;
import com.mark.zumo.client.core.view.Navigator;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.app.fcm.StoreFcmIdService;
import com.mark.zumo.client.store.app.fcm.StoreFcmService;
import com.mark.zumo.client.store.viewmodel.AcceptedOrderViewModel;
import com.wonderkiln.blurkit.BlurLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 13.
 */
public class AcceptedOrderActivity extends BaseActivity {

    @BindView(R.id.blur_filter) BlurLayout blurFilter;

    private AcceptedOrderViewModel acceptedOrderViewModel;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_accepted_order);
        ButterKnife.bind(this);

        inflateFragments();
        Navigator.addBlurFilter(blurFilter);

        acceptedOrderViewModel = ViewModelProviders.of(this).get(AcceptedOrderViewModel.class);

        startService(new Intent(this, StoreFcmService.class));
        startService(new Intent(this, StoreFcmIdService.class));
        acceptedOrderViewModel.findCustomer(this);
    }

    private void inflateFragments() {
        Fragment consoleFragment = Fragment.instantiate(this, OrderConsoleFragment.class.getName());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.console_fragment, consoleFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Navigator.setBlurLayoutVisible(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Navigator.removeBlurFilter(blurFilter);
    }

}
