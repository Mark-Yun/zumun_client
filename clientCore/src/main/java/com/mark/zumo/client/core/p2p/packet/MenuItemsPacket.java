package com.mark.zumo.client.core.p2p.packet;

import com.mark.zumo.client.core.entity.MenuItem;

import java.util.List;

/**
 * Created by mark on 18. 5. 5.
 */

public class MenuItemsPacket extends BasePacket {

    private static PacketHelper<MenuItemsPacket> helper;
    public final List<MenuItem> menuItems;

    public MenuItemsPacket(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public static MenuItemsPacket deserialize(byte[] bytes) {
        MenuItemsPacket menuItemsPacket = helper().deserializeInternal(bytes);
        return menuItemsPacket;
    }

    private static PacketHelper<MenuItemsPacket> helper() {
        if (helper == null) helper = new PacketHelper<>();
        return helper;
    }

    public byte[] serialize() {
        return helper().serializeInternal(this);
    }

    @Override
    Type type() {
        return Type.MENU_ITEM_LIST;
    }
}
