package de.lightfall.core.common;

import de.lightfall.core.common.packet.*;

public enum PacketRegistry {

    ;

    private final Class<? extends Packet> packetClass;

    private PacketRegistry(Class<? extends Packet> packetClass) {
        this.packetClass = packetClass;
    }

    public static int getPacketID(Class clazz) {
        String sName = clazz.getName();
        for (PacketRegistry value : values()) {
            if (value.getPacketClass().getName().equals(sName)) {
                return value.ordinal();
            }
        }
        return -1;
    }

    public Class<? extends Packet> getPacketClass() {
        return packetClass;
    }
}
