package com.pepperoni.orbweaver.packets;

import java.util.HashMap;
import java.util.Map;

public enum PlayerUpdateType
{
	MODEL,
	LOCATION,
	TEAM,
	STATUS,
	CHAT_MESSAGE;

	private static final Map<Integer, PlayerUpdateType> indexToEnumMap = new HashMap<>();

	static
	{
		int index = 0;
		for (PlayerUpdateType playerUpdateType : PlayerUpdateType.values())
		{
			playerUpdateType.index = index++;
			indexToEnumMap.put(playerUpdateType.index, playerUpdateType);
		}
	}

	private int index;

	public static PlayerUpdateType fromIndex(int index)
	{
		return indexToEnumMap.get(index);
	}

	public int getIndex()
	{
		return index;
	}
}
