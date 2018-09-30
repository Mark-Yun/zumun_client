/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.place.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.core.view.TouchResponse;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.menu.MenuActivity;
import com.mark.zumo.client.customer.view.menu.MenuFragment;
import com.mark.zumo.client.customer.viewmodel.PlaceViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 19.
 */
public class NearbyStoreAdapter extends RecyclerView.Adapter<NearbyStoreAdapter.StoreViewHolder> {

    private final PlaceViewModel placeViewModel;
    private final LifecycleOwner lifecycleOwner;

    private List<Store> storeList;

    public NearbyStoreAdapter(final PlaceViewModel placeViewModel, final LifecycleOwner lifecycleOwner) {
        this.placeViewModel = placeViewModel;
        this.lifecycleOwner = lifecycleOwner;
        storeList = new ArrayList<>();
    }

    public void setStoreList(final List<Store> storeList) {
        this.storeList = storeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        int resId = R.layout.card_view_place;
        View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StoreViewHolder holder, final int position) {
        Store store = storeList.get(position);
        holder.title.setText(store.name);
        placeViewModel.distanceFrom(store.latitude, store.longitude)
                .observe(lifecycleOwner, holder.distance::setText);

        if (!TextUtils.isEmpty(store.thumbnailUrl)) {
            GlideApp.with(holder.itemView.getContext())
                    .load(store.thumbnailUrl)
                    .apply(GlideUtils.storeImageOptions())
                    .transition(GlideUtils.storeTransitionOptions())
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            TouchResponse.small();
            Intent intent = new Intent(holder.itemView.getContext(), MenuActivity.class);
            intent.putExtra(MenuFragment.KEY_STORE_UUID, store.uuid);
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) TextView title;
        @BindView(R.id.distance) TextView distance;
        @BindView(R.id.image) ImageView image;

        StoreViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
