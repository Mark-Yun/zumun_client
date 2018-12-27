/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.store.fragment.registrationlist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.store.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 12. 26.
 */
class StoreRegistrationRequestAdapter extends RecyclerView.Adapter<StoreRegistrationRequestAdapter.ViewHolder> {

    private final StoreRegistrationRequestSelectListener listener;
    private final List<StoreRegistrationRequest> requestList;

    StoreRegistrationRequestAdapter(final StoreRegistrationRequestSelectListener listener) {
        this.listener = listener;
        requestList = new CopyOnWriteArrayList<>();
    }

    void setStoreRegistrationRequestList(List<StoreRegistrationRequest> storeRegistrationRequestList) {
        this.requestList.clear();
        this.requestList.addAll(storeRegistrationRequestList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_store_registration_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        StoreRegistrationRequest storeRegistrationRequest = requestList.get(position);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    interface StoreRegistrationRequestSelectListener {
        void onSelectStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
