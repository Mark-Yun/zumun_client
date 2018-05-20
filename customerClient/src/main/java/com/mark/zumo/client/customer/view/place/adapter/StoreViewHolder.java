package com.mark.zumo.client.customer.view.place.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mark.zumo.client.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 19.
 */
public class StoreViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title) TextView title;
    @BindView(R.id.distance) TextView distance;
    @BindView(R.id.image) ImageView image;

    StoreViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
