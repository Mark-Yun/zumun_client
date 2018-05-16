package com.mark.zumo.server.store.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mark.zumo.server.store.R;
import com.mark.zumo.server.store.view.order.OrderConsoleFragment;
import com.mark.zumo.server.store.view.order.OrderControllerFragment;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 13.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        inflateFragments();
    }

    private void inflateFragments() {
        Fragment consoleFragment = Fragment.instantiate(this, OrderConsoleFragment.class.getName());
        Fragment controllerFragment = Fragment.instantiate(this, OrderControllerFragment.class.getName());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.console_fragment, consoleFragment)
                .add(R.id.controller_fragment, controllerFragment)
                .commit();
    }
}
