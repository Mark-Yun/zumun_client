package com.mark.zumo.client.store.model;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.mark.zumo.client.core.database.entity.PairedBluetoothDevice;
import com.mark.zumo.client.core.device.bluetooth.BluetoothDeviceListener;
import com.mark.zumo.client.core.device.bluetooth.BluetoothDeviceProvider;
import com.mark.zumo.client.core.repository.PairedBluetoothDeviceRepository;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Emitter;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 19. 5. 18.
 */
public enum StorePrinterManager implements BluetoothDeviceListener {
    INSTANCE;

    private static final String TAG = "StorePrinterManager";

    private final BluetoothDeviceProvider bluetoothDeviceProvider;
    private final PairedBluetoothDeviceRepository pairedBluetoothDeviceRepository;
    private final Observable<Set<BluetoothDevice>> bondBluetoothDeviceObservable;

    private Emitter<Set<BluetoothDevice>> bondBluetoothDeviceObservableEmitter;

    StorePrinterManager() {
        bluetoothDeviceProvider = BluetoothDeviceProvider.INSTANCE;
        pairedBluetoothDeviceRepository = PairedBluetoothDeviceRepository.INSTANCE;
        bluetoothDeviceProvider.addListener(this);

        bondBluetoothDeviceObservable = Observable.create(emitter -> {
            bondBluetoothDeviceObservableEmitter = emitter;
            emitter.onNext(getBondedBluetoothPrinter());
        });
    }

    public void fetchConnectPairedBluetoothPrinter(final Context context) {
        startDiscovery(context)
                .flatMapMaybe(this::filterPairedBluetoothDevice)
                .flatMapMaybe(this::connectSocket)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private Maybe<BluetoothDevice> filterPairedBluetoothDevice(final BluetoothDevice bluetoothDevice) {
        return pairedBluetoothDeviceRepository.getAllPairedBluetoothDeviceList()
                .subscribeOn(Schedulers.io())
                .flatMapObservable(Observable::fromIterable)
                .any(pairedBluetoothDevice -> pairedBluetoothDevice.address.equals(bluetoothDevice.getAddress()))
                .toMaybe()
                .map(x -> bluetoothDevice);
    }

    public Observable<Set<BluetoothDevice>> getBondedBluetoothPrinterSet() {
        return bondBluetoothDeviceObservable
                .subscribeOn(Schedulers.io());
    }

    public Maybe<BluetoothDevice> connectSocket(final BluetoothDevice bluetoothDevice) {
        return bluetoothDeviceProvider.connectSocket(bluetoothDevice)
                .retry()
                .flatMap(this::savePairedBluetoothDevice)
                .subscribeOn(Schedulers.io());
    }

    private Maybe<BluetoothDevice> savePairedBluetoothDevice(final BluetoothDevice bluetoothDevice) {
        return Maybe.create(emitter -> {
            Log.d(TAG, "savePairedBluetoothDevice: name=" + bluetoothDevice.getName());
            PairedBluetoothDevice pairedBluetoothDevice = PairedBluetoothDevice.from(bluetoothDevice);
            pairedBluetoothDeviceRepository.insertPairedBluetoothDevice(pairedBluetoothDevice);
            emitter.onSuccess(bluetoothDevice);
            emitter.onComplete();
        });
    }

    public Observable<BluetoothDevice> startDiscovery(final Context context) {
        return bluetoothDeviceProvider.startDiscovery(context)
                .filter(this::isPrinterDevice)
                .distinct(BluetoothDevice::getAddress)
                .subscribeOn(Schedulers.io());
    }

    public boolean isDiscovering() {
        return bluetoothDeviceProvider.isDiscovering();
    }

    public boolean isEnabled() {
        return bluetoothDeviceProvider.isEnabled();
    }

    private boolean isPrinterDevice(final BluetoothDevice bluetoothDevice) {
        BluetoothClass bluetoothClass = bluetoothDevice.getBluetoothClass();
        int majorDeviceClass = bluetoothClass.getMajorDeviceClass();
        return majorDeviceClass == BluetoothClass.Device.Major.IMAGING;
    }

    @Override
    public void onDeviceStateChanged(final BluetoothDevice bluetoothDevice, final int state) {
        Log.d(TAG, "onDeviceStateChanged: name=" + bluetoothDevice.getName() + " state=" + state);

    }

    @Override
    public void onAclConnected(final BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onAclConnected: " + bluetoothDevice.getName());
        if (bondBluetoothDeviceObservableEmitter != null) {
            bondBluetoothDeviceObservableEmitter.onNext(getBondedBluetoothPrinter());
        }
    }

    @Override
    public void onBondStateChanged(final BluetoothDevice bluetoothDevice, final int prevBondState, final int newBondState) {
        Log.d(TAG, "onBondStateChanged: name=" + bluetoothDevice + " prevBondState=" + prevBondState + " newBondState=" + newBondState);
        if (prevBondState == BluetoothDevice.BOND_BONDING && newBondState == BluetoothDevice.BOND_BONDED) {
            if (bondBluetoothDeviceObservableEmitter != null) {
                bondBluetoothDeviceObservableEmitter.onNext(getBondedBluetoothPrinter());
            }
        }
    }

    @Override
    public void onAclDisconnected(final BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onAclDisconnected: " + bluetoothDevice.getName());
        if (bondBluetoothDeviceObservableEmitter != null) {
            bondBluetoothDeviceObservableEmitter.onNext(getBondedBluetoothPrinter());
        }
    }

    @Override
    public void onParingRequested(final BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onParingRequested: " + bluetoothDevice.getName());

    }

    private Set<BluetoothDevice> getBondedBluetoothPrinter() {
        Set<BluetoothDevice> bondedBluetoothPrinterSet = new HashSet<>();
        for (BluetoothDevice bluetoothDevice : bluetoothDeviceProvider.getBondedDevices()) {
            if (isPrinterDevice(bluetoothDevice)) {
                bondedBluetoothPrinterSet.add(bluetoothDevice);
            }
        }

        return bondedBluetoothPrinterSet;
    }
}
