/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.menu;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.model.entity.Cart;
import com.mark.zumo.client.customer.view.TouchResponse;
import com.mark.zumo.client.customer.view.cart.CartActivity;
import com.mark.zumo.client.customer.viewmodel.MainViewModel;
import com.mark.zumo.client.customer.viewmodel.MenuViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 5. 10.
 */
public class MenuFragment extends Fragment {

    public static final String TAG = "MenuFragment";

    public static final String KEY_STORE_UUID = "store_uuid";
    public static final String KEY_IS_D2D = "is_d2d";

    @BindView(R.id.store_cover_image) ImageView storeCoverImage;
    @BindView(R.id.store_cover_title) TextView storeCoverTitle;

    @BindView(R.id.store_cart_badge_image) ImageView cartBadgeImage;
    @BindView(R.id.store_cart_badge_text) TextView cartBadgeText;

    @BindView(R.id.menu_recycler_view) RecyclerView recyclerView;

    private MenuAdapter menuAdapter;
    private MenuViewModel menuViewModel;
    private MainViewModel mainViewModel;

    private String storeUuid;
    private boolean isD2D;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuViewModel = ViewModelProviders.of(this).get(MenuViewModel.class);
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        storeUuid = getArguments().getString(KEY_STORE_UUID);
        isD2D = getArguments().getBoolean(KEY_IS_D2D);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, rootView);

        inflateStoreCover();
        inflateMenuRecyclerView();
        inflateCartBadge();
        return rootView;
    }

    private void inflateMenuRecyclerView() {
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        // specify an menuAdapter (see also next example)
        menuAdapter = new MenuAdapter(this, menuViewModel, getFragmentManager());
        recyclerView.setAdapter(menuAdapter);

        //TODO: remove test code
        isD2D = false;
        if (isD2D) {
            mainViewModel.requestMenuItemList(storeUuid).observe(getActivity(), this::onLoadMenuItemList);
        } else {
            menuViewModel.getMenuItemList(getActivity()).observe(this, this::onLoadMenuItemList);
        }
    }

    private void inflateCartBadge() {
        menuViewModel.getCart(storeUuid)
                .observe(this, this::onLoadCart);
    }

    private void onLoadMenuItemList(List<Menu> menuList) {
        menuAdapter.setMenuList(menuList);
        menuAdapter.notifyDataSetChanged();
    }

    private void inflateStoreCover() {
        menuViewModel.getStore(storeUuid).observe(this, this::onLoadStore);
    }

    private void onLoadStore(Store store) {
        GlideApp.with(this)
                .load(store.coverImageUrl)
                .apply(GlideUtils.storeCoverImageOptions())
                .transition(GlideUtils.storeCoverTransitionOptions())
                .into(storeCoverImage);

        storeCoverTitle.setText(store.name);
    }

    private void onLoadCart(Cart cart) {
        int cartCount = cart.getTotalAmount();
        cartBadgeText.setText(String.valueOf(cartCount));

        boolean hasMenuInCart = cartCount > 0;
        cartBadgeImage.setVisibility(hasMenuInCart ? View.VISIBLE : View.GONE);
        cartBadgeText.setVisibility(hasMenuInCart ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.store_cart_button)
    void onClickCartButton() {
        TouchResponse.small();
        LiveData<Cart> cartLiveData = menuViewModel.getCart(storeUuid);

        Observer<Cart> observer = new Observer<Cart>() {
            @Override
            public void onChanged(@Nullable final Cart cart) {

                if (cart == null || cart.getItemCount() == 0) {
                    Toast.makeText(getActivity(), R.string.theres_no_item_in_cart, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getContext(), CartActivity.class);
                    intent.putExtra(CartActivity.KEY_STORE_UUID, storeUuid);
                    startActivityForResult(intent, CartActivity.REQUEST_CODE);
                }

                cartLiveData.removeObserver(this);
            }
        };
        cartLiveData.observe(this, observer);

    }
}
