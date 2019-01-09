/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.sign.store.fragment.registrationlist;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.util.DateUtil;
import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.store.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
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

    void onNewStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest) {
        this.requestList.add(storeRegistrationRequest);
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

        holder.storeName.setText(storeRegistrationRequest.storeName);
        if (!TextUtils.isEmpty(storeRegistrationRequest.uuid) && storeRegistrationRequest.uuid.length() > 5) {
            holder.requestId.setText(storeRegistrationRequest.uuid.substring(0, 4));
        }

        int statusStringRes = StoreRegistrationResult.Status.REQUESTED.stringRes;
        int statusColorRes = StoreRegistrationResult.Status.REQUESTED.colorRes;
        if (storeRegistrationRequest.resultList != null && storeRegistrationRequest.resultList.size() > 0) {
            try {
                StoreRegistrationResult.Status status = StoreRegistrationResult.Status.valueOf(storeRegistrationRequest.resultList.get(0).status);
                statusStringRes = status.stringRes;
                statusColorRes = status.colorRes;
            } catch (IllegalArgumentException e) {
            }
        }

        holder.status.setText(statusStringRes);
        holder.status.setTextColor(ContextHolder.getContext().getResources().getColor(statusColorRes));
        holder.createdDate.setText(DateUtil.getLocalSimpleTime(storeRegistrationRequest.createdDate));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    interface StoreRegistrationRequestSelectListener {
        void onSelectStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.request_id) AppCompatTextView requestId;
        @BindView(R.id.store_name) AppCompatTextView storeName;
        @BindView(R.id.status) AppCompatTextView status;
        @BindView(R.id.created_date) AppCompatTextView createdDate;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
