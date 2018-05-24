package com.mark.zumo.client.customer.view.menu.detail;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.mark.zumo.client.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 24.
 */
class MultiSelectViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.name) AppCompatTextView name;
    @BindView(R.id.value) ListView value;

    MultiSelectViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
