package com.pepperoni.orbweaver.packets.outgoing;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.OutgoingPacket;
import com.pepperoni.orbweaver.packets.PacketType;
import java.io.IOException;

public class RequestServerInfo extends OutgoingPacket
{
	public RequestServerInfo(OrbWeaverPlugin plugin) throws IOException
	{
		super(plugin, PacketType.SERVER_INFO);
		this.send();
	}
}
