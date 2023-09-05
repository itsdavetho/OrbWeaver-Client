package com.pepperoni.prophunt;


import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PropHuntUser {
    private final PropHuntTwoPlugin plugin;
    private final Client client;
    private String playerName = null;

    private WorldPoint lastLocation = null;
    private String token = null;
    private String groupId = null;
    private boolean loggedIn = false;
    private int world;

    public PropHuntUser(PropHuntTwoPlugin plugin, Client client) {
        this.plugin = plugin;
        this.client = client;
    }

    public void login() throws IOException {
        if (plugin.getSocket() == null || getUsername() == null) {
            plugin.configureServer();
        }

        if (playerName != null) {
            List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.USER_LOGIN, "unauthorized");
            byte[] username = playerName.getBytes(StandardCharsets.UTF_8);
            byte[] password = plugin.getConfig().password().getBytes(StandardCharsets.UTF_8);
            byte[] worldBuffer = new byte[2];
            ByteBuffer.wrap(worldBuffer).putShort((short) this.world);

            List<byte[]> bufferList = new ArrayList<>();
            bufferList.add(new byte[]{(byte) username.length, (byte) password.length});
            bufferList.add(username);
            bufferList.add(password);
            bufferList.add(worldBuffer);

            packet.addAll(bufferList);

            plugin.getPacketHandler().sendPacket(packet);
        } else {
            System.out.println("Failed to login: local player not found");
        }
    }

    public void logout() {
        if (getLoggedIn() && token != null && plugin.getSocket() != null) {
            List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.USER_LOGOUT, token);
            plugin.getPacketHandler().sendPacket(packet);
			this.setLoggedIn(false);
			this.token = null;
			this.groupId = "";
			System.out.println("yes");
        }
    }

    public boolean getLoggedIn() {
        return this.loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
        plugin.getPanel().updateLoginLogoutButton();
    }

    public void createGroup(String jwt) {
        List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.GROUP_NEW, jwt);
        plugin.getPacketHandler().sendPacket(packet);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
        plugin.getPanel().setGroupTextField(groupId);
        plugin.getPanel().updateLeaveJoinGroupButton();
    }

    public void joinGroup(String groupId) throws UnsupportedEncodingException {
        if (getLoggedIn() && getJWT() != null && getGroupId() == null) {
            List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.GROUP_JOIN, getJWT());
            byte[] groupBuffer = groupId.getBytes(StandardCharsets.UTF_8);
            List<byte[]> bufferList = new ArrayList<>();
            bufferList.add(new byte[]{(byte) groupBuffer.length});
            bufferList.add(groupBuffer);
            packet.addAll(bufferList);
            plugin.getPacketHandler().sendPacket(packet);
        } else {
            plugin.sendPrivateMessage("Could not join group. Are you logged in or already in a group?");
        }
    }

    public void leaveGroup() {
		System.out.println(getLoggedIn() + " " + getJWT());
        if (getLoggedIn() && getJWT() != null) {
            List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.GROUP_LEAVE, getJWT());
            plugin.getPacketHandler().sendPacket(packet);
        } else {
            plugin.sendPrivateMessage("There was an error while trying to leave the group.");
        }
    }

    public String getJWT() {
        return token;
    }

    public void setJWT(String token) {
        this.token = token;
    }

    public String getGameStatus() {
        return "inactive";
    }

	public void setLocation(WorldPoint loc) {
		this.lastLocation = loc;

		List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.PLAYER_UPDATE, getJWT());
		//System.out.println("updating location");

		ByteBuffer buffer = ByteBuffer.allocate(1 + 2 + 2 + 1 + 2); //2 bytes for x, 2 bytes for y, 1 byte for z, and 2 bytes for orientation
		buffer.put((byte) PlayerUpdate.LOCATION.getIndex());
		buffer.putShort((short) loc.getX());
		buffer.putShort((short) loc.getY());
		buffer.put((byte) loc.getPlane());
		buffer.putShort((short) 512);
		System.out.println("updating location: " + loc.getX() + " " + loc.getY() + " " + loc.getPlane());
		byte[] locationData = buffer.array();

		packet.add(locationData);

		plugin.getPacketHandler().sendPacket(packet);
	}

    public WorldPoint getLastLocation() {
        return this.lastLocation;
    }

    public String getUsername() {
        return this.playerName;
    }

    public void setUsername(String username) {
        this.playerName = username;
    }

    public void setWorld(int world) {
        this.world = world;
    }
}
