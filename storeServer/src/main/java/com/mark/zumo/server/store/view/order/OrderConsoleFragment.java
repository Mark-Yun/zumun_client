package com.mark.zumo.server.store.view.order;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.lsjwzh.widget.recyclerviewpager.TabLayoutSupport;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.server.store.R;
import com.mark.zumo.server.store.viewmodel.OrderViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 16.
 */
public class OrderConsoleFragment extends Fragment {

    @BindView(R.id.order_page) RecyclerViewPager orderPage;
    @BindView(R.id.order_tab) TabLayout orderTab;
    private OrderViewModel orderViewModel;
    private OrderPageAdapter orderPageAdapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orderViewModel = ViewModelProviders.of(getActivity()).get(OrderViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_console, container, false);
        ButterKnife.bind(this, view);

        inflateOrderPage();
        bindLiveData();

        return view;
    }

    private void inflateOrderPage() {
        LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        orderPage.setLayoutManager(layout);

        orderPageAdapter = new OrderPageAdapter();
        orderPage.setAdapter(orderPageAdapter);

        TabLayoutSupport.setupWithViewPager(orderTab, orderPage, orderPageAdapter);
        orderPage.addOnPageChangedListener((i, i1) -> orderTab.setScrollPosition(i1, 0.5f, true));
    }

    private void bindLiveData() {
        orderViewModel.menuOrderList().observe(this, this::onLoadMenuOrderList);
    }

    private void onLoadMenuOrderList(List<MenuOrder> menuOrderList) {
        orderPageAdapter.setMenuOrderList(menuOrderList);
        orderPageAdapter.notifyItemInserted(menuOrderList.size() - 1);
        TabLayoutSupport.setupWithViewPager(orderTab, orderPage, orderPageAdapter);
    }
}
