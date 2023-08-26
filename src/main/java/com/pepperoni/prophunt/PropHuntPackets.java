package com.pepperoni.prophunt;

import javax.inject.Inject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Map<Integer, PacketType> indexToEnumMap = new HashMap<>();

    static {
        for (PacketType packetType : PacketType.values()) {
            indexToEnumMap.put(packetType.index, packetType);
        }
    }

    private final int index;

    PacketType(int index) {
        this.index = index;
    }

    public static PacketType fromIndex(int index) {
        return indexToEnumMap.get(index);
    }

    public int getIndex() {
        return index;
    }
}

public class PropHuntPackets {
    private final PropHuntTwoPlugin plugin;

    @Inject
    public PropHuntPackets(PropHuntTwoPlugin plugin) {
        this.plugin = plugin;
    }

    public List<byte[]> createPacket(PacketType packet, String token) {
        List<byte[]> packetList = new ArrayList<>();

        byte[] actionBuffer = new byte[1];
        actionBuffer[0] = (byte) packet.getIndex();

        byte[] jwtBuffer = token.getBytes();

        byte[] tokenSize = new byte[1];
        tokenSize[0] = (byte) jwtBuffer.length;

        packetList.add(actionBuffer);
        packetList.add(tokenSize);
        packetList.add(jwtBuffer);

        return packetList;
    }

    public void sendPacket(byte[] buffer) {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, plugin.getServerAddress(), plugin.getServerPort());
            plugin.getSocket().send(datagramPacket);
        } catch (IOException e) {
            System.err.println("Error sending packet: " + e.getMessage());
        }
    }

    public byte[] concatenateByteArrays(List<byte[]> arrays) {
        int totalLength = arrays.stream().mapToInt(array -> array.length).sum();
        byte[] result = new byte[totalLength];

        int currentIndex = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }

        return result;
    }
}