package com.pepperoni.orbweaver.packets;

import com.pepperoni.orbweaver.packets.type.ErrorMessage;
import com.pepperoni.orbweaver.packets.type.GroupInfo;
import com.pepperoni.orbweaver.packets.type.GroupLeave;
import com.pepperoni.orbweaver.packets.type.PlayerList;
import com.pepperoni.orbweaver.packets.type.PlayerUpdate;
import com.pepperoni.orbweaver.packets.type.ServerInfo;
import com.pepperoni.orbweaver.packets.type.UserGetJWT;
import java.util.HashMap;
import java.util.Map;

public class PacketRegistry
{
	private static final Map<PacketType, Class<? extends Packet>> packetRegistry = new HashMap<>();

	static
	{
		packetRegistry.put(PacketType.USER_GET_JWT, UserGetJWT.class);
		packetRegistry.put(PacketType.ERROR_MESSAGE, ErrorMessage.class);
		packetRegistry.put(PacketType.PLAYER_LIST, PlayerList.class);
		packetRegistry.put(PacketType.PLAYER_UPDATE, PlayerUpdate.class);
		packetRegistry.put(PacketType.GROUP_LEAVE, GroupLeave.class);
		packetRegistry.put(PacketType.GROUP_INFO, GroupInfo.class);
		// packetRegistry.put(PacketType.MASTER_SERVER_LIST, MasterServerList.class);
		packetRegistry.put(PacketType.SERVER_INFO, ServerInfo.class);
		// Add more mappings for other PacketType enums as needed
	}

	public static Class<? extends Packet> getHandler(PacketType packetType)
	{
		return packetRegistry.get(packetType);
	}
}