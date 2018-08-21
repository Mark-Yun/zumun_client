/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.place.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.core.view.TouchResponse;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.menu.MenuActivity;
import com.mark.zumo.client.customer.view.menu.MenuFragment;
import com.mark.zumo.client.customer.viewmodel.PlaceViewModel;

import io.reactivex.exceptions.OnErrorNotImplementedException;

/**
 * Created by mark on 18. 5. 19.
 */
final class ViewHolderUtils {

    static final int HEADER_TYPE = 0;
    static final int BODY_TYPE = 1;
    static final int FOOTER_TYPE = 2;

    private static final int HEADER_RES = R.layout.card_view_place_header;
    private static final int BODY_RES = R.layout.card_view_place;
    private static final int FOOTER_RES = R.layout.card_view_place_footer;

    private ViewHolderUtils() {
        /*Empty Body*/
    }

    static RecyclerView.ViewHolder inflate(ViewGroup parent, int viewType) {
        int resId;
        switch (viewType) {
            case HEADER_TYPE:
                resId = HEADER_RES;
                View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                return new HeaderViewHolder(view);

            case FOOTER_TYPE:
                resId = FOOTER_RES;
                view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                return new FooterViewHolder(view);

            case BODY_TYPE:
                resId = BODY_RES;
                view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                return new StoreViewHolder(view);
            default:
                throw new OnErrorNotImplementedException(new Throwable());
        }
    }

    static void inject(final StoreViewHolder storeViewHolder, final Store store,
                       final PlaceViewModel placeViewModel, final LifecycleOwner lifecycleOwner) {
        storeViewHolder.title.setText(store.name);
        placeViewModel.distanceFrom(store.latitude, store.longitude)
                .observe(lifecycleOwner, storeViewHolder.distance::setText);

        GlideApp.with(storeViewHolder.itemView.getContext())
                .load(store.thumbnailUrl)
                .apply(GlideUtils.storeImageOptions())
                .transition(GlideUtils.storeTransitionOptions())
                .into(storeViewHolder.image);

        storeViewHolder.itemView.setOnClickListener(v -> {
            TouchResponse.small();
            Intent intent = new Intent(storeViewHolder.itemView.getContext(), MenuActivity.class);
            intent.putExtra(MenuFragment.KEY_STORE_UUID, store.uuid);
            storeViewHolder.itemView.getContext().startActivity(intent);
        });
    }
}
