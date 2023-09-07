package com.pepperoni.orbweaver.Packets;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.Players.OrbWeaverPlayer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class OrbWeaverPacketHandler
{
	private final OrbWeaverPlugin plugin;

	public OrbWeaverPacketHandler(OrbWeaverPlugin plugin)
	{
		this.plugin = plugin;
	}

	public static void debugPacket(DatagramPacket packet)
	{
		byte[] data = packet.getData();
		int length = packet.getLength();
		String remoteAddress = packet.getAddress().getHostAddress();
		int remotePort = packet.getPort();

		String packetContent = new String(data, 0, length, StandardCharsets.UTF_8);

		System.out.println("Received packet from " + remoteAddress + ":" + remotePort);
		System.out.println("Packet content: " + packetContent);
	}

	public void handlePacket(DatagramPacket packet)
	{
		int offset = 0;
		byte[] data = packet.getData();
		byte packetType = data[0];

		if (packetType < 0 || packetType >= OrbWeaverPacketType.values().length)
		{
			System.out.println("invalid prop hunt packet received");
			return;
		}
		offset++;

		if (packetType == OrbWeaverPacketType.USER_GET_JWT.getIndex())
		{
			int size = 1;
			Utf8Serializer.Utf8SerializedData utf8Data = Utf8Serializer.serialize(data, size, offset);
			offset = utf8Data.offset;
			plugin.getUser().setJWT(utf8Data.data[0]);
			plugin.getUser().setLoggedIn(true);
		}
		else if (packetType == OrbWeaverPacketType.ERROR_MESSAGE.getIndex())
		{
			ByteBuffer buffer = ByteBuffer.wrap(data, offset, packet.getLength());
			short dataValue = buffer.getShort();
			//  if (Errors.Errors[dataValue] != null) {
			System.out.println("ERROR RECV: " + dataValue);
			//}
		}
		else if (packetType == OrbWeaverPacketType.PLAYER_LIST.getIndex()) // a list of players on the server with an ID attached
		{
			try
			{
				HashMap<Short, OrbWeaverPlayer> players = new HashMap<>();
				int length = ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF); // packet length 2 byte uint16
				offset += 2;

				while (offset < length)
				{
					int userId = ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF); // userid 2 byte uint16
					offset += 2;
					int usernameLength = data[offset] & 0xFF; // username length 1byte uint8
					offset++;
					String username = new String(data, offset, usernameLength, StandardCharsets.UTF_8); // username utf8 * usernameLength
					offset += usernameLength;

					// Process the user data (userId and username)
					System.out.println("user: " + userId + ", usernameLength: " + usernameLength + ", username: " + username);
					OrbWeaverPlayer orbWeaverPlayer = new OrbWeaverPlayer(username);
					players.put((short) userId, orbWeaverPlayer);
				}

				plugin.updatePlayers(players);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (packetType == OrbWeaverPacketType.PLAYER_UPDATE.getIndex()) // update a specific player (e.g. location, chat)
		{
			plugin.updatePlayer(data);
		}
		else if (packetType == OrbWeaverPacketType.GROUP_INFO.getIndex())
		{
			int creatorUsernameLength = packet.getData()[offset];
			offset++;
			int groupIdLength = packet.getData()[offset];
			offset++;

			byte[] creatorUsernameBuffer = new byte[creatorUsernameLength];
			System.arraycopy(packet.getData(), offset, creatorUsernameBuffer, 0, creatorUsernameLength);
			String creatorUsername = new String(creatorUsernameBuffer, StandardCharsets.UTF_8);
			offset += creatorUsernameLength;

			byte[] groupIdBuffer = new byte[groupIdLength];
			System.arraycopy(packet.getData(), offset, groupIdBuffer, 0, groupIdLength);
			String groupId = new String(groupIdBuffer, StandardCharsets.UTF_8);
			offset += groupIdLength;
			System.out.println("Received group info (creator: " + creatorUsername + ", GID: " + groupId + ")");
			plugin.getUser().setGroupId(groupId);
		}
		else if (packetType == OrbWeaverPacketType.GROUP_LEAVE.getIndex())
		{
			plugin.sendPrivateMessage("You have left the Prop Hunt group");
			plugin.getUser().setGroupId(null);
		}
		else
		{
			System.out.println("Unknown MSG recv: " + ByteBuffer.wrap(data) + " action " + packetType);
		}
	}

	public List<byte[]> createPacket(OrbWeaverPacketType packet, String token)
	{
		List<byte[]> packetList = new ArrayList<>();

		byte[] actionBuffer = new byte[1];
		actionBuffer[0] = (byte) packet.getIndex();
		if (token == null)
		{
			token = "unauthorized";
		}
		byte[] jwtBuffer = token.getBytes();

		byte[] tokenSize = new byte[1];
		tokenSize[0] = (byte) jwtBuffer.length;

		packetList.add(actionBuffer);
		packetList.add(tokenSize);
		packetList.add(jwtBuffer);

		return packetList;
	}
	public void sendPacket(List<byte[]> packet)
	{
		try
		{
			byte[] buffer = concatenateByteArrays(packet);
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, plugin.getServerAddress(), plugin.getServerPort());
			plugin.getSocket().send(datagramPacket);
		}
		catch (IOException e)
		{
			System.err.println("Error sending packet: " + e.getMessage());
		}
	}

	public byte[] concatenateByteArrays(List<byte[]> arrays)
	{
		int totalLength = arrays.stream().mapToInt(array -> array.length).sum();
		byte[] result = new byte[totalLength];

		int currentIndex = 0;
		for (byte[] array : arrays)
		{
			System.arraycopy(array, 0, result, currentIndex, array.length);
			currentIndex += array.length;
		}

		return result;
	}
}
