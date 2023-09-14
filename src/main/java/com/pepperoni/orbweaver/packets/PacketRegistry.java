package com.pepperoni.orbweaver.packets;

import com.pepperoni.orbweaver.packets.incoming.server.ErrorMessage;
import com.pepperoni.orbweaver.packets.incoming.group.GroupInfo;
import com.pepperoni.orbweaver.packets.incoming.group.GroupLeave;
import com.pepperoni.orbweaver.packets.incoming.player.LoggedOut;
import com.pepperoni.orbweaver.packets.incoming.group.PlayerList;
import com.pepperoni.orbweaver.packets.incoming.player.PlayerUpdate;
import com.pepperoni.orbweaver.packets.incoming.server.Info;
import com.pepperoni.orbweaver.packets.incoming.player.UserGetJWT;
import com.pepperoni.orbweaver.packets.incoming.world.Model;
import java.util.HashMap;
import java.util.Map;

public class PacketRegistry
{
	private static final Map<PacketType, Class<? extends IncomingPacket>> packetRegistry = new HashMap<>();

	static
	{
		packetRegistry.put(PacketType.USER_GET_JWT, UserGetJWT.class);
		packetRegistry.put(PacketType.ERROR_MESSAGE, ErrorMessage.class);
		packetRegistry.put(PacketType.PLAYER_LIST, PlayerList.class);
		packetRegistry.put(PacketType.PLAYER_UPDATE, PlayerUpdate.class);
		packetRegistry.put(PacketType.GROUP_LEAVE, GroupLeave.class);
		packetRegistry.put(PacketType.GROUP_INFO, GroupInfo.class);
		// packetRegistry.put(PacketType.MASTER_SERVER_LIST, MasterServerList.class);
		packetRegistry.put(PacketType.SERVER_INFO, Info.class);
		packetRegistry.put(PacketType.WORLD_MODEL, Model.class);
		packetRegistry.put(PacketType.LOGGED_OUT, LoggedOut.class);

		// Add more mappings for other PacketType enums as needed
	}

	public static Class<? extends IncomingPacket> getHandler(PacketType packetType)
	{
		return packetRegistry.get(packetType);
	}
}