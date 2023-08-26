package com.pepperoni.prophunt;

import java.util.HashMap;
import java.util.Map;

public class PropHuntPackets {
    public static void main(String[] args) {
        // Example usage
        PacketType packetType = PacketType.USER_LOGIN;
        int packetIndex = packetType.getIndex();
        System.out.println("Packet type index: " + packetIndex);
    }
}

enum PacketType {
    USER_LOGIN(0),
    USER_GET_JWT(1),
    USER_LOGOUT(2),

    GROUP_NEW(3),
    GROUP_JOIN(4),
    GROUP_USERS(5),
    GROUP_INFO(6),
    GROUP_START_GAME(7),
    GROUP_END_GAME(8),
    GROUP_SET_STAGE(9),
    GROUP_NOTIFY(10),

    PLAYER_PROP(11),
    PLAYER_LOCATION(12),
    PLAYER_ORIENTATION(13),
    PLAYER_NOTIFY(14),

    ERROR_MESSAGE(15);

    private final int index;
    private static final Map<Integer, PacketType> indexToEnumMap = new HashMap<>();

    static {
        for (PacketType packetType : PacketType.values()) {
            indexToEnumMap.put(packetType.index, packetType);
        }
    }

    PacketType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static PacketType fromIndex(int index) {
        return indexToEnumMap.get(index);
    }
}