package com.mark.zumo.client.core.p2p.packet;

import com.mark.zumo.client.core.entity.EntityHelper;

/**
 * Created by mark on 18. 5. 5.
 */

public class Packet<T> {

    private T object;
    private byte[] bytes;
    private PacketType packetType;
    private PacketHelper helper;

    public Packet(T object) {
        this.object = object;
        this.packetType = PacketType.typeOf(object);
        this.bytes = serialize();
    }

    public Packet(byte[] bytes) {
        this.bytes = bytes;
        this.object = deserialize(bytes);
        this.packetType = PacketType.typeOf(object);
    }

    public T get() {
        return object;
    }

    public byte[] asByteArray() {
        return bytes;
    }

    private T deserialize(byte[] bytes) {
        Object o = helper().deserializeInternal(bytes);
        return (T) o;
    }

    private PacketHelper helper() {
        if (helper == null) helper = new PacketHelper();
        return helper;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    private byte[] serialize() {
        return helper().serializeInternal(packetType, object);
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, Packet.class);
    }
}
