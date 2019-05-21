package com.mark.zumo.client.store.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;

import com.mark.zumo.client.store.model.StorePrinterManager;

import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mark on 19. 5. 21.
 */
public class EquipmentViewModel extends AndroidViewModel {

    private final StorePrinterManager storePrinterManager;
    private final CompositeDisposable compositeDisposable;

    public EquipmentViewModel(@NonNull final Application application) {
        super(application);

        storePrinterManager = StorePrinterManager.INSTANCE;
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<BluetoothDevice> startDiscovery(final Context context) {
        MediatorLiveData<BluetoothDevice> liveData = new MediatorLiveData<>();

        storePrinterManager.startDiscovery(context)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<BluetoothDevice> connectBluetoothDevice(final BluetoothDevice bluetoothDevice) {

        MediatorLiveData<BluetoothDevice> liveData = new MediatorLiveData<>();

        storePrinterManager.connectSocket(bluetoothDevice)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public LiveData<Set<BluetoothDevice>> getBondedDeviceSet() {

        MediatorLiveData<Set<BluetoothDevice>> liveData = new MediatorLiveData<>();

        storePrinterManager.getBondedBluetoothPrinterSet()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(liveData::setValue)
                .doOnSubscribe(compositeDisposable::add)
                .subscribe();

        return liveData;
    }

    public boolean isDiscovering() {
        return storePrinterManager.isDiscovering();
    }

    public boolean isEnabled() {
        return storePrinterManager.isEnabled();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
