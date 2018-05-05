package com.mark.zumo.client.core.p2p.packet;

import java.util.Arrays;

/**
 * Created by mark on 18. 5. 5.
 */

public class PacketFactory {

    public static BasePacket createPacket(byte[] bytes) {
        byte[] typeAsByte = Arrays.copyOfRange(bytes, 0, 3);
        int typeOrdinal = BasePacket.Type.byteToint(typeAsByte);
        BasePacket.Type type = BasePacket.Type.valueOf(typeOrdinal);

        switch (type) {
            case MENU_ITEM_LIST:
                return MenuItemsPacket.deserialize(bytes);
        }

        throw new UnsupportedOperationException();
    }

    public static MenuItemsPacket convert(BasePacket basePacket) {
        if (basePacket instanceof MenuItemsPacket) {
            return (MenuItemsPacket) basePacket;
        }

        throw new ClassCastException();
    }
}
