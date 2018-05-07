package com.mark.zumo.client.customer.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.view.Window;

import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.SampleViewModel;

/**
 * Created by mark on 18. 4. 30.
 */

public class MainActivity extends AppCompatActivity {
    private SampleViewModel sampleViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new AutoTransition());
        }
        setContentView(R.layout.activity_main);
        setActionBar();
        sampleViewModel = ViewModelProviders.of(this).get(SampleViewModel.class);
    }

    private void doSomeThing() {
        sampleViewModel.doSomeThing();
    }

    private void setActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}