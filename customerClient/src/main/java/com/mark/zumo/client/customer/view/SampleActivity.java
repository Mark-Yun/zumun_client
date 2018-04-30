package com.mark.zumo.client.customer.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mark.zumo.client.customer.viewmodel.SampleViewModel;

/**
 * Created by mark on 18. 4. 30.
 */

public class SampleActivity extends AppCompatActivity {
    private SampleViewModel sampleViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        sampleViewModel = ViewModelProviders.of(this).get(SampleViewModel.class);
    }

    private void doSomeThing() {
        sampleViewModel.doSomeThing();
    }
}
