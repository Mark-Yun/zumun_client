package com.mark.zumo.client.core.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mark.zumo.client.core.database.entity.PairedBluetoothDevice;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by mark on 19. 5. 21.
 */
@Dao
public interface PairedBluetoothDeviceDao {

    @Query("SELECT * FROM " + PairedBluetoothDevice.Scheme.TABLE)
    Maybe<List<PairedBluetoothDevice>> getAllPairedBluetoothDeviceList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPairedBluetoothDevice(PairedBluetoothDevice pairedBluetoothDevice);

    @Query("SELECT * FROM " + PairedBluetoothDevice.Scheme.TABLE + " WHERE address LIKE :address")
    Maybe<PairedBluetoothDevice> getPairedBluetoothDeviceList(String address);

    @Delete
    void deletePairedBluetoothDevice(PairedBluetoothDevice pairedBluetoothDevice);
}
