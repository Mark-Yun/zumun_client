package com.mark.zumo.client.customer.view.cart;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.CartViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 5. 22.
 */
public class CartActivity extends AppCompatActivity {

    public static final String KEY_STORE_UUID = "store_uuid";

    private String storeUuid;

    @BindView(R.id.store_cover_image) AppCompatImageView storeImage;
    @BindView(R.id.store_cover_title) AppCompatTextView storeTitle;
    @BindView(R.id.cart_item_recycler_view) RecyclerView cartItemRecyclerView;
    @BindView(R.id.total_price) AppCompatTextView totalPrice;

    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeUuid = getIntent().getStringExtra(KEY_STORE_UUID);
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel.class);

        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        inflateView(storeUuid);
    }

    @OnClick(R.id.pay_button)
    void onClickPayButton() {
        Toast.makeText(this, "onClickPayButton", Toast.LENGTH_SHORT).show();
    }

    private void inflateView(String storeUuid) {
        cartViewModel.getStore(storeUuid).observe(this, this::onLoadStore);

        cartItemRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        cartItemRecyclerView.setLayoutManager(layoutManager);

        CartMenuAdapter cartMenuAdapter = new CartMenuAdapter(cartViewModel, this);
        cartItemRecyclerView.setAdapter(cartMenuAdapter);

        cartViewModel.getCartItemList(storeUuid).observe(this, cartMenuAdapter::setCartItemList);
        cartViewModel.getTotalPrice(storeUuid).observe(this, totalPrice::setText);
    }

    private void onLoadStore(Store store) {
        storeTitle.setText(store.name);

        GlideApp.with(this)
                .load(store.coverImageUrl)
                .transition(GlideUtils.storeCoverTransitionOptions())
                .apply(GlideUtils.storeCoverImageOptions())
                .into(storeImage);
    }
}
