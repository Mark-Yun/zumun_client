/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.p2p;

import android.util.Log;

import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.mark.zumo.client.core.p2p.packet.Packet;
import com.mark.zumo.client.core.util.context.ContextHolder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by mark on 18. 4. 30.
 */

class NearbyUtil {

    private static final String TAG = "NearbyUtil";

    static String convertString(BleSignal bleSignal) {
        return "BleSignal rssi=" + bleSignal.getRssi()
                + " txPower=" + bleSignal.getTxPower();
    }

    static String convertString(Message message) {
        return "Message type=" + message.getType()
                + " nameSpace=" + message.getNamespace()
                + " content=" + Arrays.toString(message.getContent());
    }

    static String convertString(Distance distance) {
        return "Distance accuracy=" + distance.getAccuracy()
                + " meters=" + distance.getMeters();
    }

    static byte[] bytesFrom(Payload payload) {
        int payloadType = payload.getType();
        switch (payloadType) {
            case Payload.Type.BYTES:
                return payload.asBytes();

            case Payload.Type.STREAM:
                InputStream inputStream = Objects.requireNonNull(payload.asStream()).asInputStream();
                try {
                    return IOUtils.toByteArray(inputStream);
                } catch (IOException e) {
                    Log.e(TAG, "bytesFrom: ", e);
                }
                return null;

            case Payload.Type.FILE:
                File file = Objects.requireNonNull(payload.asFile()).asJavaFile();
                try {
                    return FileUtils.readFileToByteArray(Objects.requireNonNull(file));
                } catch (IOException e) {
                    Log.e(TAG, "bytesFrom: ", e);
                    return null;
                }
            default:
                return null;
        }
    }

    static Payload payloadFrom(Packet packet) {
        byte[] bytes = packet.asByteArray();

        Payload payload;
        if (bytes.length > Connections.MAX_BYTES_DATA_SIZE) {
            String fileName = ContextHolder.getContext().getCacheDir() + "/tmp";
            writeToFile(bytes, fileName);
            File tempFile = new File(fileName);
            try {
                payload = Payload.fromFile(tempFile);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "payloadFrom: ", e);
                payload = null;
            }
        } else {
            payload = Payload.fromBytes(bytes);
        }

        return payload;
    }

    private static void writeToFile(byte[] data, String fileName) {
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            out.write(data);
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToFile: ", e);
        }
    }
}
