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

	GROUP_START_GAME, // TODO: Start the game
	GROUP_END_GAME,   // TODO: End the game
	GROUP_SET_STAGE,  // TODO: Set the play area

	WORLD_MODEL, // used in adding/removing objects

	PLAYER_LIST,
	PLAYER_UPDATE, // PLAYER_UPDATE opcode is followed by PlayerUpdate type found below

	ERROR_MESSAGE,

	MASTER_SERVER_POLL,
	MASTER_SERVER_LIST,
	SERVER_INFO;

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

