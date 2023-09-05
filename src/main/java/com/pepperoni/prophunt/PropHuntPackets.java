package com.pepperoni.prophunt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

enum PacketType
{
	USER_LOGIN,
	USER_GET_JWT,
	USER_LOGOUT,

	GROUP_NEW,
	GROUP_JOIN,
	GROUP_LEAVE,
	GROUP_INFO,

	GROUP_START_GAME,
	GROUP_END_GAME,
	GROUP_SET_STAGE,

	PLAYER_LIST,
	PLAYER_UPDATE, // PLAYER_UPDATE opcode is followed by PlayerUpdate type found below

	ERROR_MESSAGE;

	private static final Map<Integer, PacketType> indexToEnumMap = new HashMap<>();

	static
	{
		int index = 0;
		for (PacketType packetType : PacketType.values())
		{
			packetType.index = index++;
			indexToEnumMap.put(packetType.index, packetType);
		}
	}

	private int index;

	public static PacketType fromIndex(int index)
	{
		return indexToEnumMap.get(index);
	}

	public int getIndex()
	{
		return index;
	}
}

enum PlayerUpdate
{
	PROP,
	LOCATION,
	TEAM,
	STATUS;

	private static final Map<Integer, PlayerUpdate> indexToEnumMap = new HashMap<>();

	static
	{
		int index = 0;
		for (PlayerUpdate playerUpdate : PlayerUpdate.values())
		{
			playerUpdate.index = index++;
			indexToEnumMap.put(playerUpdate.index, playerUpdate);
		}
	}

	private int index;

	public static PlayerUpdate fromIndex(int index)
	{
		return indexToEnumMap.get(index);
	}

	public int getIndex()
	{
		return index;
	}
}

public class PropHuntPackets
{
	private final PropHuntTwoPlugin plugin;

	@Inject
	public PropHuntPackets(PropHuntTwoPlugin plugin)
	{
		this.plugin = plugin;
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