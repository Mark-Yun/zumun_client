package com.mark.zumo.client.core.p2p.packet;

import java.io.Serializable;

/**
 * Created by mark on 18. 5. 5.
 */

public abstract class BasePacket implements Serializable {

    abstract Type type();

    public enum Type {
        MENU_ITEM_LIST;

        public static int byteToint(byte[] arr) {
            return (arr[0] & 0xff) << 24
                    | (arr[1] & 0xff) << 16
                    | (arr[2] & 0xff) << 8
                    | (arr[3] & 0xff);
        }

        public static Type valueOf(int ordinal) {
            for (Type type : values()) {
                if (ordinal == type.ordinal())
                    return type;
            }

            throw new UnsupportedOperationException();
        }

        public byte[] getBytes() {
            int value = ordinal();
            byte[] byteArray = new byte[4];
            byteArray[0] = (byte) (value >> 24);
            byteArray[1] = (byte) (value >> 16);
            byteArray[2] = (byte) (value >> 8);
            byteArray[3] = (byte) (value);
            return byteArray;
        }

    }

}
