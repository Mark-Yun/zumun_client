package com.mark.zumo.client.store.model;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.mark.zumo.client.core.device.bluetooth.BluetoothDeviceListener;
import com.mark.zumo.client.core.device.bluetooth.BluetoothDeviceProvider;
import com.mark.zumo.client.store.model.entity.print.command.CommandBuilder;

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
    private final Observable<Set<BluetoothDevice>> bondBluetoothDeviceObservable;

    private Emitter<Set<BluetoothDevice>> bondBluetoothDeviceObservableEmitter;

    StorePrinterManager() {
        bluetoothDeviceProvider = BluetoothDeviceProvider.INSTANCE;
        bluetoothDeviceProvider.addListener(this);

        bondBluetoothDeviceObservable = Observable.create(emitter -> {
            bondBluetoothDeviceObservableEmitter = emitter;
            emitter.onNext(getBondedBluetoothPrinterSet());
        });
    }

    public void fetchConnectPairedBluetoothPrinter() {
        Observable.fromIterable(getBondedBluetoothPrinterSet())
                .filter(bluetoothDevice -> !bluetoothDeviceProvider.isConnectedDevice(bluetoothDevice))
                .flatMapMaybe(this::connectSocket)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Observable<Set<BluetoothDevice>> getBondedBluetoothPrinterSetObservable() {
        return bondBluetoothDeviceObservable
                .subscribeOn(Schedulers.io());
    }

    public void disconnectDevice(final BluetoothDevice bluetoothDevice) {

        if (!bluetoothDeviceProvider.isBondedBluetoothDevice(bluetoothDevice)) {
            Log.e(TAG, "disconnectDevice: device is not bonded.");
            return;
        }

        if (!bluetoothDeviceProvider.isConnectedDevice(bluetoothDevice)) {
            Log.e(TAG, "disconnectDevice: device is not connected.");
            return;
        }

        bluetoothDeviceProvider.disconnectDevice(bluetoothDevice);
    }

    public Maybe<BluetoothDevice> connectSocket(final BluetoothDevice bluetoothDevice) {
        return bluetoothDeviceProvider.connectSocket(bluetoothDevice)
                .retry(10)
                .subscribeOn(Schedulers.io());
    }

    public Observable<BluetoothDevice> startDiscovery(final Context context) {
        return bluetoothDeviceProvider.startDiscovery(context)
                .filter(this::isPrinterDevice)
                .distinct(BluetoothDevice::getAddress)
                .subscribeOn(Schedulers.io());
    }

    public void stopDiscovery() {
        bluetoothDeviceProvider.stopDiscovery();
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

    private Bitmap loadBitmapFromView(View view) {
        int specWidth = View.MeasureSpec.makeMeasureSpec(view.getWidth() /* any */, View.MeasureSpec.EXACTLY);
        int specHeight = View.MeasureSpec.makeMeasureSpec(view.getHeight() /* any */, View.MeasureSpec.EXACTLY);
        view.measure(specWidth, specHeight);
        int questionWidth = view.getMeasuredWidth();
        int questionHeight = view.getMeasuredHeight();

        Bitmap bitmap = Bitmap.createBitmap(questionWidth, questionHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(canvas);
        return bitmap;
    }

    public void printView(final View view) {
        Observable.fromCallable(() ->
                new CommandBuilder()
                        .setNewLine(2)
                        .setBitmap(loadBitmapFromView(view))
                        .setNewLine(4)
                        .build())
                .flatMap(Observable::fromIterable)
                .flatMapMaybe(bluetoothDeviceProvider::sendData)
                .subscribeOn(Schedulers.newThread())
                .subscribe();
    }

    @Override
    public void onDeviceStateChanged(final BluetoothDevice bluetoothDevice, final int state) {
        Log.d(TAG, "onDeviceStateChanged: name=" + bluetoothDevice.getName() + " state=" + state);
    }

    @Override
    public void onAclConnected(final BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onAclConnected: " + bluetoothDevice.getName());
    }

    @Override
    public void onAclDisconnected(final BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onAclDisconnected: " + bluetoothDevice.getName());

        if (bluetoothDeviceProvider.isBondedBluetoothDevice(bluetoothDevice)) {
            bluetoothDeviceProvider.connectSocket(bluetoothDevice)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    @Override
    public void onBondStateChanged(final BluetoothDevice bluetoothDevice, final int prevBondState, final int newBondState) {
        Log.d(TAG, "onBondStateChanged: name=" + bluetoothDevice +
                " prevBondState=" + prevBondState +
                " newBondState=" + newBondState);

        if (bondBluetoothDeviceObservableEmitter != null) {
            bondBluetoothDeviceObservableEmitter.onNext(getBondedBluetoothPrinterSet());
        }
    }

    @Override
    public void onParingRequested(final BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onParingRequested: " + bluetoothDevice.getName());
    }

    private Set<BluetoothDevice> getBondedBluetoothPrinterSet() {
        Set<BluetoothDevice> bondedBluetoothPrinterSet = new HashSet<>();
        for (BluetoothDevice bluetoothDevice : bluetoothDeviceProvider.getBondedDevices()) {
            if (isPrinterDevice(bluetoothDevice)) {
                bondedBluetoothPrinterSet.add(bluetoothDevice);
            }
        }

        return bondedBluetoothPrinterSet;
    }
}
