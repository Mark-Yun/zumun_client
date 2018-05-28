package com.mark.zumo.client.customer.view.cart;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.model.entity.CartItem;
import com.mark.zumo.client.customer.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 28.
 */
public class CartMenuAdapter extends RecyclerView.Adapter<CartMenuAdapter.CartMenuViewHolder> {

    private CartViewModel cartViewModel;
    private LifecycleOwner lifecycleOwner;

    private List<CartItem> cartItemList;

    CartMenuAdapter(final CartViewModel cartViewModel, final LifecycleOwner lifecycleOwner) {
        this.cartViewModel = cartViewModel;
        this.lifecycleOwner = lifecycleOwner;

        cartItemList = new ArrayList<>();
    }

    void setCartItemList(final List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartMenuAdapter.CartMenuViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_cart_item, parent, false);
        return new CartMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartMenuAdapter.CartMenuViewHolder holder, final int position) {
        CartItem cartItem = cartItemList.get(position);

        cartViewModel.getMenu(cartItem.menuUuid).observe(lifecycleOwner, menu -> {
            holder.menuName.setText(menu.name);

            GlideApp.with(holder.itemView.getContext())
                    .load(menu.imageUrl)
                    .apply(GlideUtils.cartMenuImageOptions())
                    .transition(GlideUtils.cartMenuTransitionOptions())
                    .into(holder.menuImage);

            cartViewModel.getCartItemPriceLiveData(cartItem.storeUuid, position).observe(lifecycleOwner, holder.menuPrice::setText);

            holder.cartOptionRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext());
            holder.cartOptionRecyclerView.setLayoutManager(layoutManager);

            CartOptionAdapter cartOptionAdapter = new CartOptionAdapter(cartViewModel, lifecycleOwner);
            holder.cartOptionRecyclerView.setAdapter(cartOptionAdapter);

            cartOptionAdapter.setOrderDetailList(new ArrayList<>(cartItem.getOrderDetailList()));
        });

        holder.removeButton.setOnClickListener(v -> cartViewModel.removeCartItem(position));
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    class CartMenuViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.menu_name) AppCompatTextView menuName;
        @BindView(R.id.menu_image) AppCompatImageView menuImage;
        @BindView(R.id.remove_button) AppCompatImageButton removeButton;
        @BindView(R.id.menu_price) AppCompatTextView menuPrice;
        @BindView(R.id.cart_option_recycler_view) RecyclerView cartOptionRecyclerView;

        CartMenuViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
