package com.pepperoni.orbweaver.packets.outgoing.user;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.OutgoingPacket;
import com.pepperoni.orbweaver.packets.PacketType;
import java.io.IOException;

public class Logout extends OutgoingPacket
{

	public Logout(OrbWeaverPlugin plugin) throws IOException
	{
		super(plugin, PacketType.USER_LOGOUT);

		this.send();
	}
}
