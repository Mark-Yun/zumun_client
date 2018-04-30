package com.mark.zumo.client.core.p2p;

import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;

/**
 * Created by mark on 18. 4. 30.
 */

class NearbyUtil {

    static String convertString(BleSignal bleSignal) {
        return "BleSignal rssi=" + bleSignal.getRssi()
                + " txPower=" + bleSignal.getTxPower();
    }

    static String convertString(Message message) {
        return "Message type=" + message.getType()
                + " nameSpace=" + message.getNamespace()
                + " content=" + message.getContent();
    }

    static String convertString(Distance distance) {
        return "Distance accuracy=" + distance.getAccuracy()
                + " meters=" + distance.getMeters();
    }
}
