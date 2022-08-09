package de.cloud.core.common

import de.cloud.core.common.packet.*

enum class PacketRegistry(val packetClass: Class<out Packet>) {

    PACKET_IN_LINK(PacketInLink::class.java),
    PACKET_OUT_LINK(PacketOutLink::class.java),
    PACKET_IN_REQUEST_LINK(PacketInRequestLink::class.java),
    PACKET_OUT_REQUEST_LINK(PacketOutRequestLink::class.java),
    PACKET_IN_AUTHENTICATION(PacketInAuthentication::class.java),
    PACKET_OUT_AUTHENTICATION(PacketOutAuthentication::class.java);

    companion object {
        @JvmStatic
        fun getPacketID(clazz: Class<*>): Int {
            val sName = clazz.name
            for (value in values()) {
                if (value.packetClass.name == sName) {
                    return value.ordinal
                }
            }
            return -1
        }
    }
}