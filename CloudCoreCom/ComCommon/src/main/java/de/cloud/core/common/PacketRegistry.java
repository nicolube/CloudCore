package de.cloud.core.common;

import de.cloud.core.common.packet.Packet;
import de.cloud.core.common.packet.PacketInAuthentication;

public enum PacketRegistry {

    PACKET_IN_AUTHENTICATION(PacketInAuthentication.class);

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