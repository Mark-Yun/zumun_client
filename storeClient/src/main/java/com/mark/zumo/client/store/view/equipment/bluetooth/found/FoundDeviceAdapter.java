package com.mark.zumo.client.store.view.equipment.bluetooth.found;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 19. 5. 21.
 */
class FoundDeviceAdapter extends RecyclerView.Adapter<FoundDeviceAdapter.ViewHolder> {

    private final OnSelectDeviceListener onSelectDeviceListener;
    private final List<BluetoothDevice> deviceList;

    FoundDeviceAdapter(final OnSelectDeviceListener onSelectDeviceListener) {
        this.onSelectDeviceListener = onSelectDeviceListener;
        deviceList = new CopyOnWriteArrayList<>();
    }

    void clear() {
        notifyItemRangeRemoved(0, deviceList.size());
        deviceList.clear();
    }

    void onFoundNewDevice(final BluetoothDevice bluetoothDevice) {
        deviceList.add(bluetoothDevice);
        notifyItemInserted(deviceList.indexOf(bluetoothDevice));
    }

    void onDeviceConnected(final BluetoothDevice bluetoothDevice) {
        notifyItemRemoved(deviceList.indexOf(bluetoothDevice));
        deviceList.remove(bluetoothDevice);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_found_device, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        BluetoothDevice bluetoothDevice = deviceList.get(position);

        viewHolder.deviceName.setText(bluetoothDevice.getName());
        viewHolder.deviceAddress.setText(bluetoothDevice.getAddress());
        viewHolder.deviceIcon.setImageResource(R.drawable.ic_print_blue_grey_600_48dp);

        viewHolder.itemView.setOnClickListener(v -> onSelectDeviceListener.onSelectDevice(bluetoothDevice));
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    interface OnSelectDeviceListener {
        void onSelectDevice(BluetoothDevice bluetoothDevice);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.device_icon) AppCompatImageView deviceIcon;
        @BindView(R.id.device_name) AppCompatTextView deviceName;
        @BindView(R.id.device_address) AppCompatTextView deviceAddress;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
