/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.p2p.packet;

import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.core.database.entity.MenuOrder;

import java.util.List;

/**
 * Created by mark on 18. 5. 5.
 */
public enum PacketType {
    MENU_ORDER,
    REQUEST,
    RESPONSE,
    MENU_ITEM_LIST;

    public static int byteToInt(byte[] arr) {
        return (arr[0] & 0xff) << 24
                | (arr[1] & 0xff) << 16
                | (arr[2] & 0xff) << 8
                | (arr[3] & 0xff);
    }

    public static PacketType valueOf(int ordinal) {
        for (PacketType type : values()) {
            if (ordinal == type.ordinal())
                return type;
        }

        throw new UnsupportedOperationException();
    }

    public static <T> PacketType typeOf(T t) {
        if (t instanceof List<?>) {
            List<?> tAsList = (List<?>) t;
            if (isMenuItemListType(tAsList))
                return MENU_ITEM_LIST;
        } else if (t instanceof MenuOrder) {
            return MENU_ORDER;
        } else if (t instanceof Request) {
            return REQUEST;
        } else if (t instanceof Response) {
            return RESPONSE;
        }

        throw new UnsupportedOperationException("unsupported type-" + t);
    }

    private static boolean isMenuItemListType(List<?> list) {
        for (Object obj : list) {
            if (!(obj instanceof Menu)) {
                return false;
            }
        }

        return true;
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
