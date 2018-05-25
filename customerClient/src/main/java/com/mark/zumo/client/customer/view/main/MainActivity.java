package com.mark.zumo.client.customer.view.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.AutoTransition;
import android.view.Window;

import com.mark.zumo.client.core.app.BaseActivity;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.Navigator;
import com.wonderkiln.blurkit.BlurLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 4. 30.
 */

public class MainActivity extends BaseActivity {

    @BindView(R.id.blur_filter) BlurLayout blurFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new AutoTransition());
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        inflateMenuFragment();

        Navigator.setBlurFilter(blurFilter);
    }

    private void inflateMenuFragment() {
        Fragment mainBodyFragment = Fragment.instantiate(this, MainBodyFragment.class.getName());
        Fragment mainHeaderFragment = Fragment.instantiate(this, MainHeaderFragment.class.getName());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_body_fragment, mainBodyFragment)
                .add(R.id.main_header_fragment, mainHeaderFragment)
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
        Navigator.setBlurFilter(null);
    }
}