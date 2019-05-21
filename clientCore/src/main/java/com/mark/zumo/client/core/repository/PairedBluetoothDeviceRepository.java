package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.database.AppDatabaseProvider;
import com.mark.zumo.client.core.database.dao.PairedBluetoothDeviceDao;
import com.mark.zumo.client.core.database.entity.PairedBluetoothDevice;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by mark on 19. 5. 21.
 */
public enum PairedBluetoothDeviceRepository {
    INSTANCE;

    private final PairedBluetoothDeviceDao pairedBluetoothDeviceDao;

    PairedBluetoothDeviceRepository() {
        pairedBluetoothDeviceDao = AppDatabaseProvider.INSTANCE.pairedBluetoothDeviceDao;
    }

    public Maybe<List<PairedBluetoothDevice>> getAllPairedBluetoothDeviceList() {
        return pairedBluetoothDeviceDao.getAllPairedBluetoothDeviceList();
    }

    public Maybe<PairedBluetoothDevice> getPairedBluetoothDeviceList(String address) {
        return pairedBluetoothDeviceDao.getPairedBluetoothDeviceList(address);
    }

    public void insertPairedBluetoothDevice(PairedBluetoothDevice pairedBluetoothDevice) {
        pairedBluetoothDeviceDao.insertPairedBluetoothDevice(pairedBluetoothDevice);
    }

    public void deletePairedBluetoothDevice(PairedBluetoothDevice pairedBluetoothDevice) {
        pairedBluetoothDeviceDao.deletePairedBluetoothDevice(pairedBluetoothDevice);
    }
}
