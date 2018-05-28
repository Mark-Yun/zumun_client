package com.mark.zumo.client.customer.view.cart;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 28.
 */
public class CartOptionAdapter extends RecyclerView.Adapter<CartOptionAdapter.OptionViewHolder> {

    private CartViewModel cartViewModel;
    private LifecycleOwner lifecycleOwner;

    private List<OrderDetail> orderDetailList;

    CartOptionAdapter(final CartViewModel cartViewModel, final LifecycleOwner lifecycleOwner) {
        this.cartViewModel = cartViewModel;
        this.lifecycleOwner = lifecycleOwner;

        orderDetailList = new ArrayList<>();
    }

    void setOrderDetailList(final List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_cart_item_menu_option, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OptionViewHolder holder, final int position) {
        OrderDetail orderDetail = orderDetailList.get(position);
        cartViewModel.getMenuOption(orderDetail.menuOptionUuid).observe(lifecycleOwner, menuOption -> {
            holder.name.setText(menuOption.name);
            holder.value.setText(menuOption.value);
        });
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    class OptionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.value) AppCompatTextView value;

        private OptionViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
