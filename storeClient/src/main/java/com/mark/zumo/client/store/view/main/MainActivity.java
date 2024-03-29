/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.main;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.transformation.LinearGradientTransformation;
import com.mark.zumo.client.core.view.BaseActivity;
import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.equipment.EquipmentFragment;
import com.mark.zumo.client.store.view.main.fragment.storeselect.StoreSelectFragment;
import com.mark.zumo.client.store.view.order.OrderFragment;
import com.mark.zumo.client.store.view.setting.fragment.SettingMainFragment;
import com.mark.zumo.client.store.view.sign.store.fragment.StoreRegistrationFragment;
import com.mark.zumo.client.store.view.sign.user.UserSignActivity;
import com.mark.zumo.client.store.view.witdraw.fragment.WithdrawMainFragment;
import com.mark.zumo.client.store.viewmodel.MainViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 7. 1.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nav_view) NavigationView navView;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;

    private MainViewModel mainViewModel;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.icon_anim_fade_in, R.anim.icon_anim_fade_out);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LayoutInflater.from(this).inflate(R.layout.nav_header_main, navView);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.content_desc_navigation_drawer_open, R.string.content_desc_navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        checkSessionAndInflateMainFragmentIfPossible();
    }

    private void checkSessionAndInflateMainFragmentIfPossible() {
        mainViewModel.hasSessionStoreAsync().observe(this, this::onSessionStoreLoaded);
    }

    private void inflateMainFragment() {

        inflateStoreInformation();
        mainViewModel.findCustomer(this);
        Menu menu = navView.getMenu();
        if (menu == null) {
            return;
        }

        MenuItem firstItem = menu.getItem(0);
        if (firstItem == null) {
            return;
        }

        firstItem.setChecked(true);
        onNavigationItemSelected(firstItem);
    }

    private void onSessionStoreLoaded(boolean hasSessionStore) {
        Log.d(TAG, "onSessionStoreLoaded: hasSessionStore=" + hasSessionStore);
        if (hasSessionStore) {
            inflateMainFragment();
        } else {
            StoreSelectFragment storeSelectFragment = StoreSelectFragment.newInstance()
                    .onSelectStore(this::onSelectedStore)
                    .onClickStoreRegistration(this::onClickStoreRegistration);
            updateMainFragment(storeSelectFragment);
        }
    }

    private void onClickStoreRegistration() {
        Fragment fragment = Fragment.instantiate(this, StoreRegistrationFragment.class.getName());
        updateMainFragment(fragment);
    }

    private void onSelectedStore(Store store) {
        if (store == null) {
            return;
        }

        inflateMainFragment();
    }

    private void inflateStoreInformation() {
        mainViewModel.getSessionStoreObservable().observe(this, this::onStoreUpdated);
    }

    private void onStoreUpdated(Store store) {
        if (store == null) {
            return;
        }

        setTitle(store.name);

        AppCompatTextView name = navView.findViewById(R.id.name);
        AppCompatTextView address = navView.findViewById(R.id.address);
        AppCompatImageView coverImage = navView.findViewById(R.id.cover_image);
        AppCompatImageView thumbnailImage = navView.findViewById(R.id.thumbnail_image);

        name.setText(store.name);
        address.setText(store.address);
        GlideApp.with(this)
                .load(store.thumbnailUrl)
                .apply(RequestOptions.circleCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(thumbnailImage);

        GlideApp.with(this)
                .load(store.coverImageUrl)
                .apply(RequestOptions.centerCropTransform())
                .transform(new LinearGradientTransformation(this))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(coverImage);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu_order) {
            Fragment fragment = Fragment.instantiate(this, OrderFragment.class.getName());
            updateMainFragment(fragment);
        } else if (id == R.id.nav_setting) {
            Fragment fragment = Fragment.instantiate(this, SettingMainFragment.class.getName());
            updateMainFragment(fragment);
        } else if (id == R.id.nav_store_registration) {
            Fragment fragment = Fragment.instantiate(this, StoreRegistrationFragment.class.getName());
            updateMainFragment(fragment);
        } else if (id == R.id.nav_withdraw) {
            updateMainFragment(WithdrawMainFragment.newInstance());
        } else if (id == R.id.nav_equipment) {
            updateMainFragment(EquipmentFragment.newInstance());
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateMainFragment(final Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_fragment, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sign_out:
                onSelectSignOutOption();
                return true;
            case R.id.select_store:
                Fragment fragment = Fragment.instantiate(this, StoreSelectFragment.class.getName());
                updateMainFragment(fragment);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSelectSignOutOption() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sign_out_dialog_title)
                .setMessage(R.string.sign_out_dialog_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> signOut())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                .create().show();
    }

    private void signOut() {
        mainViewModel.signOut();
        UserSignActivity.start(this);
    }
}
