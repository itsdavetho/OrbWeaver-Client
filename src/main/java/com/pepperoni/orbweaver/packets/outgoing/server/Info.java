package com.pepperoni.orbweaver.packets.outgoing.server;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.OutgoingPacket;
import com.pepperoni.orbweaver.packets.PacketType;
import java.io.IOException;

public class Info extends OutgoingPacket
{
	public Info(OrbWeaverPlugin plugin) throws IOException
	{
		super(plugin, PacketType.SERVER_INFO);
		this.send();
	}
}
