package com.mark.zumo.client.customer.view.menu;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.MenuItemViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 10.
 */
public class MenuFragment extends Fragment {

    @BindView(R.id.menu_recycler_view) RecyclerView recyclerView;

    private MenuAdapter menuAdapter;
    private MenuItemViewModel menuItemViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuItemViewModel = ViewModelProviders.of(this).get(MenuItemViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, rootView);

        inflateMenuRecyclerView();
        setupMenuItemObserver();
        return rootView;
    }

    private void inflateMenuRecyclerView() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an menuAdapter (see also next example)
        menuAdapter = new MenuAdapter();
        recyclerView.setAdapter(menuAdapter);
    }

    private void onLoadMenuItemList(List<MenuItem> menuItemList) {
        menuAdapter.setMenuItemList(menuItemList);
        menuAdapter.notifyDataSetChanged();
    }

    private void setupMenuItemObserver() {
        menuItemViewModel.getMenuItemList(getActivity())
                .observe(this, this::onLoadMenuItemList);
    }
}
