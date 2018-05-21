package com.mark.zumo.client.customer.view.place.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.customer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark on 18. 5. 19.
 */
public class NearbyStoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Store> storeList;

    public NearbyStoreAdapter() {
        storeList = new ArrayList<>();
    }

    public void setStoreList(final List<Store> storeList) {
        this.storeList = storeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return ViewHolderUtils.inflate(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.title.setText(R.string.place_near_by_header_text);
        } else if (holder instanceof FooterViewHolder) {
            /*Do Nothing*/
        } else if (holder instanceof StoreViewHolder) {
            StoreViewHolder storeViewHolder = (StoreViewHolder) holder;
            Store store = storeList.get(position - 1);
            ViewHolderUtils.inject(storeViewHolder, store);
        }
    }

    @Override
    public int getItemCount() {
        return storeList.size() + 2;
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == 0) {
            return ViewHolderUtils.HEADER_TYPE;
        } else if (position == getItemCount() - 1) {
            return ViewHolderUtils.FOOTER_TYPE;
        } else {
            return ViewHolderUtils.BODY_TYPE;
        }
    }
}