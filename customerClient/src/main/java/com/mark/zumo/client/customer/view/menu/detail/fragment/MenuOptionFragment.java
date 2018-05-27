package com.mark.zumo.client.customer.view.menu.detail.fragment;

import android.arch.lifecycle.LiveData;
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

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.view.menu.detail.MenuDetailActivity;
import com.mark.zumo.client.customer.viewmodel.MenuDetailViewModel;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 24.
 */
public class MenuOptionFragment extends Fragment {

    @BindView(R.id.menu_option_recycler_view) RecyclerView recyclerView;

    private String uuid;
    private MenuDetailViewModel menuDetailViewModel;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuDetailViewModel = ViewModelProviders.of(this).get(MenuDetailViewModel.class);
        uuid = getArguments().getString(MenuDetailActivity.KEY_MENU_UUID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_option, container, false);
        ButterKnife.bind(this, view);
        inflateRecyclerView();
        return view;
    }

    private void inflateRecyclerView() {

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        MenuOptionAdapter adapter = new MenuOptionAdapter(this, menuDetailViewModel);
        recyclerView.setAdapter(adapter);

        LiveData<Map<String, List<MenuOption>>> menuOptionMap = menuDetailViewModel.getMenuOptionMap(uuid);
        menuOptionMap.observe(this, adapter::setOptionMap);
    }
}
