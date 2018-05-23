package com.mark.zumo.client.customer.view.cart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by mark on 18. 5. 22.
 */
public class CartActivity extends AppCompatActivity {
    public static final String KEY_STORE_UUID = "store_uuid";

    private String storeUuid;
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeUuid = getIntent().getStringExtra(KEY_STORE_UUID);
        Toast.makeText(this, storeUuid, Toast.LENGTH_SHORT).show();
    }
}
