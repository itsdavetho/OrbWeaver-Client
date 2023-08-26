package com.pepperoni.prophunt;

import com.google.inject.spi.Message;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.List;


public class MessageHandler {
    private final PropHuntTwoPlugin plugin;

    public MessageHandler(PropHuntTwoPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void handleMessage(DatagramPacket message) {
        int offset = 0;
        byte action = message.getData()[0];
        if (action < 0 || action >= PacketType.values().length) {
            System.out.println("invalid prop hunt message received");
            return;
        }
        offset++;

        System.out.println("prop hunt packet received " + message.getData().toString());

        if (action == PacketType.USER_GET_JWT.getIndex()) {
            int size = 1;
            Utf8Serializer.Utf8SerializedData data = Utf8Serializer.serialize(message.getData(), size, offset);
            offset = data.offset;
            String jwt = data.data[0];
            plugin.createGroup(jwt);
        }
    }
}
