/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.place.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.menu.MenuFragment;

import io.reactivex.exceptions.OnErrorNotImplementedException;

/**
 * Created by mark on 18. 5. 19.
 */
final class ViewHolderUtils {

    static final int HEADER_TYPE = 0;
    static final int BODY_TYPE = 1;
    static final int FOOTER_TYPE = 2;

    static final int HEADER_RES = R.layout.card_view_place_header;
    static final int BODY_RES = R.layout.card_view_place;
    static final int FOOTER_RES = R.layout.card_view_place_footer;

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

    static void inject(FragmentManager fragmentManager, StoreViewHolder storeViewHolder, Store store) {
        storeViewHolder.title.setText(store.name);
        storeViewHolder.distance.setText(store.latitude + ", " + store.longitude);

        //TODO REMOVE TEST DATA
        GlideApp.with(storeViewHolder.itemView.getContext())
                .load(R.drawable.data_3_hot)
                .apply(GlideUtils.storeImageOptions())
                .transition(GlideUtils.storeTransitionOptions())
                .into(storeViewHolder.image);

        storeViewHolder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(MenuFragment.KEY_STORE_UUID, store.uuid);
            Fragment menuFragment = Fragment.instantiate(storeViewHolder.itemView.getContext(), MenuFragment.class.getName(), bundle);

            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right)
                    .add(R.id.place_main_fragment, menuFragment)
                    .addToBackStack(store.uuid)
                    .commit();
        });
    }
}
