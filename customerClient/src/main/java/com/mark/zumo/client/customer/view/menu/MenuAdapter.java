package com.mark.zumo.client.customer.view.menu;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.customer.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 10.
 */
class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private List<MenuItem> menuItemList;

    MenuAdapter() {
        menuItemList = new ArrayList<>();
    }

    public void setMenuItemList(final List<MenuItem> menuItemList) {
        this.menuItemList = menuItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        //TODO: remove Test data
        MenuItem menuItem = menuItemList.get(position);
        viewHolder.name.setText("Name : " + menuItem.name);
        viewHolder.price.setText("Price : " + menuItem.price);

        Glide.with(viewHolder.rootView)
                .load(menuItem.image)
                .into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return menuItemList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) TextView name;
        @BindView(R.id.price) TextView price;
        @BindView(R.id.image) ImageView image;
        View rootView;

        private ViewHolder(final View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
