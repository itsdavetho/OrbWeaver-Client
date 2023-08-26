package com.pepperoni.prophunt;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MessageHandler {
    private final PropHuntTwoPlugin plugin;

    public MessageHandler(PropHuntTwoPlugin plugin) {
        this.plugin = plugin;
    }

    public static void debugPacket(DatagramPacket packet) {
        byte[] data = packet.getData();
        int length = packet.getLength();
        String remoteAddress = packet.getAddress().getHostAddress();
        int remotePort = packet.getPort();

        String packetContent = new String(data, 0, length, StandardCharsets.UTF_8);

        System.out.println("Received packet from " + remoteAddress + ":" + remotePort);
        System.out.println("Packet content: " + packetContent);
    }

    public void handleMessage(DatagramPacket message) {
        debugPacket(message);
        int offset = 0;
        byte[] data = message.getData();
        byte action = data[0];

        if (action < 0 || action >= PacketType.values().length) {
            System.out.println("invalid prop hunt message received");
            return;
        }
        offset++;

        if (action == PacketType.USER_GET_JWT.getIndex()) {
            int size = 1;
            Utf8Serializer.Utf8SerializedData utf8Data = Utf8Serializer.serialize(data, size, offset);
            offset = utf8Data.offset;
            plugin.getUser().setJWT(utf8Data.data[0]);
            //plugin.createGroup(plugin.getJWT());
            plugin.getUser().setLoggedIn(true);
        } else if (action == PacketType.ERROR_MESSAGE.getIndex()) {
            // int dataValue = ByteBuffer.wrap(data, offset + 1, 2).getShort();
            //  if (Errors.Errors[dataValue] != null) {
            System.out.println("ERROR RECV: "/* + Errors.Errors[dataValue]*/);
            // }
        } else if (action == PacketType.GROUP_USERS.getIndex()) {
            List<String> usernames = new ArrayList<>();

            while (offset + 2 < data.length) { // Ensure enough bytes for username length
                int usernameLength = ByteBuffer.wrap(data, offset, 2).getShort();
                offset += 2;

                if (offset + usernameLength > data.length) {
                    break;
                }

                byte[] usernameBuffer = new byte[usernameLength];
                System.arraycopy(data, offset, usernameBuffer, 0, usernameLength);
                offset += usernameLength;
                String username = new String(usernameBuffer, StandardCharsets.UTF_8);
                usernames.add(username);
            }
            System.out.println("joined group - " + usernames.size() + " users online: ");
            System.out.println(usernames);
        } else if (action == PacketType.GROUP_INFO.getIndex()) {
            int creatorUsernameLength = message.getData()[offset];
            offset++;
            int groupIdLength = message.getData()[offset];
            offset++;

            byte[] creatorUsernameBuffer = new byte[creatorUsernameLength];
            System.arraycopy(message.getData(), offset, creatorUsernameBuffer, 0, creatorUsernameLength);
            String creatorUsername = new String(creatorUsernameBuffer, StandardCharsets.UTF_8);
            offset += creatorUsernameLength;

            byte[] groupIdBuffer = new byte[groupIdLength];
            System.arraycopy(message.getData(), offset, groupIdBuffer, 0, groupIdLength);
            String groupId = new String(groupIdBuffer, StandardCharsets.UTF_8);
            offset += groupIdLength;
            System.out.println("Received group info (creator: " + creatorUsername + ", GID: " + groupId + ")");
            plugin.getUser().setGroupId(groupId);
        } else if (action == PacketType.GROUP_LEAVE.getIndex()) {
            plugin.sendPrivateMessage("You have left the Prop Hunt group");
            plugin.getUser().setGroupId(null);
        } else {
            System.out.println("Unknown MSG recv: " + ByteBuffer.wrap(data) + " action " + action);
        }
    }
}
