package com.mark.zumo.client.customer.view.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by mark on 18. 5. 23.
 */
public class MenuDetailActivity extends AppCompatActivity {
    public static final String KEY_MENU_UUID = "menu";

    private String menuUuid;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuUuid = getIntent().getStringExtra(KEY_MENU_UUID);
        Toast.makeText(this, menuUuid, Toast.LENGTH_SHORT).show();
    }
}
