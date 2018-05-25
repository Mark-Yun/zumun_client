package com.mark.zumo.client.customer.view.menu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.core.util.glide.GlideUtils;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.Navigator;
import com.mark.zumo.client.customer.view.menu.detail.MenuDetailActivity;
import com.mark.zumo.client.customer.viewmodel.MenuViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 10.
 */
class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private List<Menu> menuList;
    private MenuViewModel menuViewModel;
    private FragmentManager fragmentManager;

    MenuAdapter(MenuViewModel menuViewModel, FragmentManager fragmentManager) {
        menuList = new ArrayList<>();
        this.menuViewModel = menuViewModel;
        this.fragmentManager = fragmentManager;
    }

    public void setMenuList(final List<Menu> menuList) {
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        Menu menu = menuList.get(position);
        viewHolder.name.setText(menu.name);
        viewHolder.price.setText(NumberFormat.getCurrencyInstance().format(menu.price));

        GlideApp.with(viewHolder.itemView.getContext())
                .load(menu.imageUrl)
                .apply(GlideUtils.menuImageOptions())
                .transition(GlideUtils.menuTransitionOptions())
                .into(viewHolder.image);

        viewHolder.itemView.setOnClickListener(v -> onClickMenu(v, menu));
        viewHolder.itemView.setOnLongClickListener(v -> onLongClickMenu(v, menu));
//        GestureDetector gestureDetector = new GestureDetector(viewHolder.itemView.getContext(), new GestureListener(() -> onLongClickMenu(viewHolder.itemView, menu)));
//        viewHolder.itemView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void onClickMenu(final View itemView, final Menu menu) {
        menuViewModel.addMenuToCart(menu);
        String snackBarText = itemView.getContext().getString(R.string.added_to_cart, menu.name);
        Snackbar.make(itemView, snackBarText, Snackbar.LENGTH_LONG)
                .setAction(R.string.cancel_action, v -> menuViewModel.removeLatestMenuFromCart())
                .show();
    }

    private boolean onLongClickMenu(final View itemView, final Menu menu) {

        Context context = itemView.getContext();
        Intent intent = new Intent(context, MenuDetailActivity.class);
        intent.putExtra(MenuDetailActivity.KEY_MENU_UUID, menu.uuid);
        context.startActivity(intent);

        Navigator.setBlurLayoutVisible(true);
        return true;
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    @FunctionalInterface
    private interface OnDoubleTap {
        void onDoubleTab();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) TextView name;
        @BindView(R.id.price) TextView price;
        @BindView(R.id.image) ImageView image;

        private ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private OnDoubleTap onDoubleTap;

        private GestureListener(final OnDoubleTap onDoubleTap) {
            this.onDoubleTap = onDoubleTap;
        }

        @Override
        public boolean onDoubleTap(final MotionEvent e) {
            this.onDoubleTap.onDoubleTab();
            return true;
        }
    }
}
