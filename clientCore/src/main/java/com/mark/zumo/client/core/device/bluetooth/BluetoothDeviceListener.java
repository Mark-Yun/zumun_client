package com.mark.zumo.client.core.device.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by mark on 19. 5. 20.
 */
public interface BluetoothDeviceListener {
    default void onDeviceStateChanged(final BluetoothDevice bluetoothDevice, final int state) { }
    default void onAclConnected(final BluetoothDevice bluetoothDevice) { }
    default void onBondStateChanged(final BluetoothDevice bluetoothDevice, final int prevBondState, final int newBondState) { }
    default void onAclDisconnected(final BluetoothDevice bluetoothDevice) { }
    default void onParingRequested(final BluetoothDevice bluetoothDevice) { }
}
