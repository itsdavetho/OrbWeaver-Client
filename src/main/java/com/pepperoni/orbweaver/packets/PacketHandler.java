package com.pepperoni.orbweaver.packets;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.type.ErrorMessage;
import com.pepperoni.orbweaver.packets.type.GroupInfo;
import com.pepperoni.orbweaver.packets.type.GroupLeave;
import com.pepperoni.orbweaver.packets.type.PlayerList;
import com.pepperoni.orbweaver.packets.type.PlayerUpdate;
import com.pepperoni.orbweaver.packets.type.UserGetJWT;
import com.pepperoni.orbweaver.players.Player;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PacketHandler
{
	private final OrbWeaverPlugin plugin;

	public PacketHandler(OrbWeaverPlugin plugin)
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

	public void handlePacket(DatagramPacket packet) throws IOException
	{
		byte[] data = packet.getData();
		byte packetType = data[0];

		if (packetType < 0 || packetType >= PacketType.values().length)
		{
			System.out.println("invalid orbweaver packet received");
			return;
		}

		if (packetType == PacketType.USER_GET_JWT.getIndex())
		{
			UserGetJWT getJWT = new UserGetJWT(data);
			getJWT.process(plugin);
		}
		else if (packetType == PacketType.ERROR_MESSAGE.getIndex())
		{
			ErrorMessage errorMessage = new ErrorMessage(data);
			errorMessage.process(plugin);
		}
		else if (packetType == PacketType.PLAYER_LIST.getIndex()) // a list of players on the server with an ID attached
		{
			PlayerList playerList = new PlayerList(data);
			playerList.process(plugin);
		}
		else if (packetType == PacketType.PLAYER_UPDATE.getIndex()) // update a specific player (e.g. location, chat)
		{
			PlayerUpdate playerUpdate = new PlayerUpdate(data);
			playerUpdate.process(plugin);
		}
		else if (packetType == PacketType.GROUP_INFO.getIndex())
		{
			GroupInfo groupInfo = new GroupInfo(data);
			groupInfo.process(plugin);
		}
		else if (packetType == PacketType.GROUP_LEAVE.getIndex())
		{
			GroupLeave groupLeave = new GroupLeave(data);
			groupLeave.process(plugin);
		}
		else
		{
			System.out.println("Unknown OP Code received: " + packetType);
		}
	}

	public List<byte[]> createPacket(PacketType packet, String token)
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
