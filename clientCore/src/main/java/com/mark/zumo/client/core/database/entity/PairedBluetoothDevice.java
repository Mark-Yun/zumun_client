package com.mark.zumo.client.core.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.database.entity.util.EntityComparator;
import com.mark.zumo.client.core.database.entity.util.EntityHelper;

/**
 * Created by mark on 19. 5. 21.
 */
@Entity(tableName = PairedBluetoothDevice.Scheme.TABLE)
public class PairedBluetoothDevice {

    @NonNull @PrimaryKey @ColumnInfo(name = Scheme.ADDRESS)
    public final String address;
    @ColumnInfo(name = Scheme.NAME)
    public final String name;
    @ColumnInfo(name = Scheme.PAIRED_DATE)
    public final long pairedDate;

    public PairedBluetoothDevice(@NonNull final String address, final String name, final long pairedDate) {
        this.address = address;
        this.name = name;
        this.pairedDate = pairedDate;
    }

    public static PairedBluetoothDevice from(final BluetoothDevice bluetoothDevice) {
        return new PairedBluetoothDevice(bluetoothDevice.getAddress(), bluetoothDevice.getName(), System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return new EntityComparator<>().test(this, obj);
    }

    public interface Scheme {
        String TABLE = "paired_bluetooth_device";
        String ADDRESS = "address";
        String NAME = "name";
        String PAIRED_DATE = "paired_date";
    }
}
