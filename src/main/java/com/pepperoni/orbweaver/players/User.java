package com.pepperoni.orbweaver.players;


import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.PacketType;
import com.pepperoni.orbweaver.packets.PlayerUpdateType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

public class User
{
	private final OrbWeaverPlugin plugin;
	private final Client client;
	@Getter
	@Setter
	private String username = null;
	@Getter
	@Setter
	private WorldPoint lastLocation = null;
	@Getter
	@Setter
	private String JWT = null;
	@Getter
	private String groupId = null;
	@Getter
	private boolean loggedIn = false;
	@Getter
	@Setter
	private int world;

	public User(OrbWeaverPlugin plugin, Client client)
	{
		this.plugin = plugin;
		this.client = client;
	}

	public void login() throws IOException
	{
		if (plugin.getSocket() == null || getUsername() == null)
		{
			plugin.configureServer();
		}

		if (getUsername() != null)
		{
			List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.USER_LOGIN, "unauthorized");
			byte[] username = getUsername().getBytes(StandardCharsets.UTF_8);
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
		}
		else
		{
			System.out.println("Failed to login: local player not found");
		}
	}

	public void logout()
	{
		if (isLoggedIn() && getJWT() != null && plugin.getSocket() != null)
		{
			List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.USER_LOGOUT, getJWT());
			plugin.getPacketHandler().sendPacket(packet);
			this.setLoggedIn(false);
			this.setJWT(null);
			this.groupId = "";
			plugin.getModelManager().removeModels();
			System.out.println("yes");
		}
	}

	public void setLoggedIn(boolean loggedIn)
	{
		this.loggedIn = loggedIn;
		plugin.getPanel().updateLoginLogoutButton();
	}

	public void createGroup(String jwt)
	{
		List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.GROUP_NEW, jwt);
		plugin.getPacketHandler().sendPacket(packet);
	}

	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
		plugin.getPanel().setGroupTextField(groupId);
		plugin.getPanel().updateLeaveJoinGroupButton();
	}

	public void joinGroup(String groupId) throws UnsupportedEncodingException
	{
		if (isLoggedIn() && getJWT() != null && getGroupId() == null)
		{
			List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.GROUP_JOIN, getJWT());
			byte[] groupBuffer = groupId.getBytes(StandardCharsets.UTF_8);
			List<byte[]> bufferList = new ArrayList<>();
			bufferList.add(new byte[]{(byte) groupBuffer.length});
			bufferList.add(groupBuffer);
			packet.addAll(bufferList);
			plugin.getPacketHandler().sendPacket(packet);
		}
		else
		{
			plugin.sendPrivateMessage("Could not join group. Are you logged in or already in a group?");
		}
	}

	public void leaveGroup()
	{
		System.out.println(isLoggedIn() + " " + getJWT());
		if (isLoggedIn() && getJWT() != null)
		{
			List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.GROUP_LEAVE, getJWT());
			plugin.getPacketHandler().sendPacket(packet);
		}
		else
		{
			plugin.sendPrivateMessage("There was an error while trying to leave the group.");
		}
	}

	public String getGameStatus()
	{
		return "inactive";
	}

	public void setLocation(WorldPoint loc, int orientation)
	{
		this.lastLocation = loc;

		List<byte[]> packet = plugin.getPacketHandler().createPacket(PacketType.PLAYER_UPDATE, getJWT());

		ByteBuffer buffer = ByteBuffer.allocate(1 + 2 + 2 + 1 + 2);
		//1 byte for update code,
		// 2 bytes for x, 2 bytes for y,
		// 1 byte for z, and 2 bytes for orientation

		buffer.put((byte) PlayerUpdateType.LOCATION.getIndex());
		buffer.putShort((short) loc.getX());
		buffer.putShort((short) loc.getY());
		buffer.put((byte) loc.getPlane());
		buffer.putShort((short) orientation);
		byte[] locationData = buffer.array();

		packet.add(locationData);

		plugin.getPacketHandler().sendPacket(packet);
	}

}
