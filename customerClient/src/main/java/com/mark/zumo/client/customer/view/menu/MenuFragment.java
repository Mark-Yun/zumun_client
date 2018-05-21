package com.mark.zumo.client.customer.view.menu;

import android.arch.lifecycle.ViewModelProviders;
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

import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.MenuItemViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 5. 10.
 */
public class MenuFragment extends Fragment {

    @BindView(R.id.store_cover_image) ImageView storeCoverImage;
    @BindView(R.id.store_cover_title) TextView storeCoverTitle;

    @BindView(R.id.store_cart_badge_image) ImageView cartBadgeImage;
    @BindView(R.id.store_cart_badge_text) TextView cartBadgeText;

    @BindView(R.id.menu_recycler_view) RecyclerView recyclerView;

    private MenuAdapter menuAdapter;
    private MenuItemViewModel menuItemViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuItemViewModel = ViewModelProviders.of(this).get(MenuItemViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, rootView);

        inflateMenuRecyclerView();
        setupMenuItemObserver();
        inflateStoreCover();
        return rootView;
    }

    private void inflateMenuRecyclerView() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // specify an menuAdapter (see also next example)
        menuAdapter = new MenuAdapter();
        recyclerView.setAdapter(menuAdapter);
    }

    private void onLoadMenuItemList(List<MenuItem> menuItemList) {
        menuAdapter.setMenuItemList(menuItemList);
        menuAdapter.notifyDataSetChanged();
    }

    private void setupMenuItemObserver() {
        menuItemViewModel.getMenuItemList(getActivity())
                .observe(this, this::onLoadMenuItemList);
    }

    private void inflateStoreCover() {
        onLoadStoreCover(null);
    }

    private void onLoadStoreCover(Store store) {
        //TODO REMOVE TEST DATA
        GlideApp.with(getActivity())
                .load(R.drawable.blue_bottle_coffee_nakameguro_1)
                .apply(GlideUtils.storeCoverImageOptions())
                .transition(GlideUtils.storeCoverTransitionOptions())
                .into(storeCoverImage);

        storeCoverTitle.setText("Remove This Data");
    }

    @OnClick(R.id.store_cart_button)
    void onClickCartButton() {
        Toast.makeText(getActivity(), "Cart Clicked!", Toast.LENGTH_SHORT).show();
    }
}
