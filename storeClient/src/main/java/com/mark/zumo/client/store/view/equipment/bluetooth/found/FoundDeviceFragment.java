package com.mark.zumo.client.store.view.equipment.bluetooth.found;

import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.viewmodel.EquipmentViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 19. 5. 21.
 */
public class FoundDeviceFragment extends Fragment {

    @BindView(R.id.find_device) AppCompatButton findDevice;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private EquipmentViewModel equipmentViewModel;
    private FoundDeviceAdapter foundDeviceAdapter;

    public static FoundDeviceFragment newInstance() {

        Bundle args = new Bundle();

        FoundDeviceFragment fragment = new FoundDeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        equipmentViewModel = ViewModelProviders.of(this).get(EquipmentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found_device, container, false);
        ButterKnife.bind(this, view);
        inflateViews();
        return view;
    }

    private void inflateViews() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        foundDeviceAdapter = new FoundDeviceAdapter(this::onSelectBluetoothDevice);
        recyclerView.setAdapter(foundDeviceAdapter);
    }

    @OnClick(R.id.find_device)
    public void onFindDeviceClicked() {
        foundDeviceAdapter.clear();
        equipmentViewModel.startDiscovery(getContext()).observe(this, foundDeviceAdapter::onFoundNewDevice);
    }

    private void onSelectBluetoothDevice(final BluetoothDevice bluetoothDevice) {
        equipmentViewModel.connectBluetoothDevice(bluetoothDevice).observe(this, foundDeviceAdapter::onDeviceConnected);
    }
}
