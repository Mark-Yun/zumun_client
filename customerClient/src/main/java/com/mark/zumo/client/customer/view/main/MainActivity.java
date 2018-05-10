package com.mark.zumo.client.customer.view.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.view.Window;

import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.menu.MenuFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 4. 30.
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new AutoTransition());
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        inflateActionBar();
        inflateMenuFragment();
    }

    private void inflateActionBar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void inflateMenuFragment() {
        Fragment menuFragment = Fragment.instantiate(this, MenuFragment.class.getName());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_fragment, menuFragment)
                .commit();
    }
}