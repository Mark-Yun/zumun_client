/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.supervisor.view.storeregistration;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.util.DateUtil;
import com.mark.zumo.client.supervisor.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 12. 30.
 */
public class StoreRegistrationListAdapter extends RecyclerView.Adapter<StoreRegistrationListAdapter.ViewHolder> {

    private final List<StoreRegistrationRequest> storeRegistrationRequestList;
    private final StoreRegistrationRequestSelectListener storeRegistrationRequestListener;

    StoreRegistrationListAdapter(StoreRegistrationRequestSelectListener storeRegistrationRequestListener) {
        this.storeRegistrationRequestListener = storeRegistrationRequestListener;
        storeRegistrationRequestList = new CopyOnWriteArrayList<>();
    }

    void setStoreRegistrationRequestList(List<StoreRegistrationRequest> storeRegistrationRequestList) {
        this.storeRegistrationRequestList.clear();
        this.storeRegistrationRequestList.addAll(storeRegistrationRequestList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_store_registration_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (position == 0) {
            return;
        }

        StoreRegistrationRequest storeRegistrationRequest = storeRegistrationRequestList.get(position - 1);

        holder.storeName.setText(storeRegistrationRequest.storeName);
        holder.ownerName.setText(storeRegistrationRequest.corporateRegistrationOwnerName);
//        holder.status.setText(storeRegistrationRequest.re);
        holder.updatedDate.setText(DateUtil.getLocalSimpleTime(storeRegistrationRequest.createdDate));

        holder.itemView.setOnClickListener(v -> storeRegistrationRequestListener.onSelectStoreRegistrationRequest(storeRegistrationRequest));
    }

    @Override
    public int getItemCount() {
        return storeRegistrationRequestList.size() + 1;
    }

    interface StoreRegistrationRequestSelectListener {
        void onSelectStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.store_name) AppCompatTextView storeName;
        @BindView(R.id.owner_name) AppCompatTextView ownerName;
        @BindView(R.id.status) AppCompatTextView status;
        @BindView(R.id.updated_date) AppCompatTextView updatedDate;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
