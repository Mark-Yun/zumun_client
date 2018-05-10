package com.mark.zumo.client.core.p2p.packet;

import android.util.Log;

import com.google.android.gms.common.util.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * Created by mark on 18. 5. 5.
 */
class PacketHelper<T> {
    private static final String TAG = "PacketHelper";

    PacketHelper() {
    }

    T deserializeInternal(byte[] data) {
        byte[] newData = Arrays.copyOfRange(data, 4, data.length);

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(newData);
            ObjectInputStream is = new ObjectInputStream(in);
            Object o = is.readObject();
            Object retObject = o;
            in.close();
            is.close();
            return (T) retObject;
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, "deserializeInternal: ", e);
        }

        return null;
    }

    byte[] serializeInternal(PacketType packetType, T object) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(object);
            byte[] bytes = out.toByteArray();
            os.close();
            out.close();

            byte[] typeAsByte = packetType.getBytes();
            return ArrayUtils.concatByteArrays(typeAsByte, bytes);
        } catch (IOException e) {
            Log.e(TAG, "serialize: ", e);
            return null;
        }
    }
}
