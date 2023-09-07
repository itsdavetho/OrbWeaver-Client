package com.pepperoni.orbweaver.Packets;

import java.util.HashMap;
import java.util.Map;

public enum OrbWeaverPlayerUpdateType
{
	PROP,
	LOCATION,
	TEAM,
	STATUS;

	private static final Map<Integer, OrbWeaverPlayerUpdateType> indexToEnumMap = new HashMap<>();

	static
	{
		int index = 0;
		for (OrbWeaverPlayerUpdateType playerUpdateType : OrbWeaverPlayerUpdateType.values())
		{
			playerUpdateType.index = index++;
			indexToEnumMap.put(playerUpdateType.index, playerUpdateType);
		}
	}

	private int index;

	public static OrbWeaverPlayerUpdateType fromIndex(int index)
	{
		return indexToEnumMap.get(index);
	}

	public int getIndex()
	{
		return index;
	}
}
