package com.pepperoni.orbweaver.packets;

import java.util.HashMap;
import java.util.Map;

public enum PacketType
{
	USER_LOGIN,
	USER_GET_JWT,
	USER_LOGOUT,
	LOGGED_OUT,

	GROUP_NEW,
	GROUP_JOIN,
	GROUP_LEAVE,
	GROUP_INFO,

	GROUP_START_GAME,
	GROUP_END_GAME,
	GROUP_SET_STAGE,

	WORLD_OBJECT,
	WORLD_NPC,

	PLAYER_LIST,
	PLAYER_UPDATE, // PLAYER_UPDATE opcode is followed by PlayerUpdate type found below

	ERROR_MESSAGE,
	MASTER_SERVER_POLL, // used to register to the master server
	MASTER_SERVER_LIST, // used to retrieve the list of registered servers
	SERVER_INFO;        // used to retrieve server info

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

