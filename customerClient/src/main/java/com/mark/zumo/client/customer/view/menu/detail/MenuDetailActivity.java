package com.mark.zumo.client.customer.view.menu.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mark.zumo.client.customer.R;

/**
 * Created by mark on 18. 5. 23.
 */
public class MenuDetailActivity extends AppCompatActivity {
    public static final String KEY_MENU_UUID = "menu";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        String menuUuid = getIntent().getStringExtra(KEY_MENU_UUID);
        inflateViews(menuUuid);
    }

    private void inflateViews(String uuid) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MENU_UUID, uuid);

        Fragment menuInfoFragment = Fragment.instantiate(this, MenuInfoFragment.class.getName(), bundle);
        Fragment menuOptionFragment = Fragment.instantiate(this, MenuOptionFragment.class.getName(), bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.menu_info_fragment, menuInfoFragment)
                .add(R.id.menu_option_fragment, menuOptionFragment)
                .commit();
    }
}
