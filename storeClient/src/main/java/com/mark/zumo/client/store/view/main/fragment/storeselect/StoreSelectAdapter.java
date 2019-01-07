/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.main.fragment.storeselect;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.store.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 19. 1. 7.
 */
public class StoreSelectAdapter extends RecyclerView.Adapter<StoreSelectAdapter.ViewHolder> {

    private final List<Store> storeList;
    private final StoreSelectListener storeSelectListener;

    StoreSelectAdapter(final StoreSelectListener storeSelectListener) {
        this.storeSelectListener = storeSelectListener;
        storeList = new CopyOnWriteArrayList<>();
    }

    void setStoreList(List<Store> storeList) {
        this.storeList.clear();
        this.storeList.addAll(storeList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_store_select, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        Store store = storeList.get(position);

        viewHolder.name.setText(store.name);
        GlideApp.with(viewHolder.thumbnailImage)
                .load(store.thumbnailUrl)
                .into(viewHolder.thumbnailImage);

        viewHolder.thumbnailImage.setOnClickListener(v -> storeSelectListener.onSelectStore(store));
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    interface StoreSelectListener {
        void onSelectStore(Store store);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail_image) AppCompatImageView thumbnailImage;
        @BindView(R.id.name) AppCompatTextView name;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
