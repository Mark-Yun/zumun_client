package com.mark.zumo.client.store.view.equipment.bluetooth.bonded;

import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.order.detail.OrderDetailFragment;
import com.mark.zumo.client.store.viewmodel.EquipmentViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 19. 5. 21.
 */
public class BondedDeviceFragment extends Fragment {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private EquipmentViewModel equipmentViewModel;
    public static BondedDeviceFragment newInstance() {

        Bundle args = new Bundle();

        BondedDeviceFragment fragment = new BondedDeviceFragment();
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
        View view = inflater.inflate(R.layout.fragment_bonded_device, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        BondedDeviceAdapter foundDeviceAdapter = new BondedDeviceAdapter(this::onSelectBluetoothDevice);
        recyclerView.setAdapter(foundDeviceAdapter);

        equipmentViewModel.getBondedDeviceSet().observe(this, foundDeviceAdapter::setDeviceSet);
    }

    private void onSelectBluetoothDevice(final BluetoothDevice bluetoothDevice) {
        new AlertDialog.Builder(getActivity())
                .setTitle(bluetoothDevice.getName())
                .setMessage(bluetoothDevice.getAddress())
                .setPositiveButton("Test Print", (dialog, which) -> testPrint())
                .setNegativeButton("Disconnect", (dialog, which) -> equipmentViewModel.disconnect(bluetoothDevice))
                .create()
                .show();
    }

    private void testPrint() {
//        Bundle arguments = new Bundle();
//        arguments.putString(OrderDetailFragment.KEY_ORDER_UUID, "1637DD2599FE46668FDEBB322BD92DEF");
//        Fragment fragment = OrderDetailFragment.instantiate(getActivity(), OrderDetailFragment.class.getName(), arguments);
//        fragment.onCreate(arguments);
//        View view = fragment.onCreateView(getLayoutInflater(), (ViewGroup) getView(), null);
        equipmentViewModel.testPrint(getView());
    }
}
