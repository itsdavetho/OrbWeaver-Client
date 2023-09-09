package com.pepperoni.orbweaver.packets.outgoing.group;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.OutgoingPacket;
import com.pepperoni.orbweaver.packets.PacketType;
import java.io.IOException;

public class NewGroup extends OutgoingPacket
{
	public NewGroup(OrbWeaverPlugin plugin) throws IOException
	{
		super(plugin, PacketType.GROUP_NEW);
		this.send();
	}
}
