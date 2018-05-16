package com.mark.zumo.server.store.view.order;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.server.store.R;
import com.mark.zumo.server.store.viewmodel.OrderViewModel;

import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 16.
 */
public class OrderControllerFragment extends Fragment {

    private OrderViewModel orderViewModel;

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
        return view;
    }
}
