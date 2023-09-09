package com.pepperoni.orbweaver.packets.outgoing.group;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.OutgoingPacket;
import com.pepperoni.orbweaver.packets.PacketType;
import java.io.IOException;

public class LeaveGroup extends OutgoingPacket
{
	public LeaveGroup(OrbWeaverPlugin plugin) throws IOException
	{
		super(plugin, PacketType.GROUP_LEAVE);
		this.send();
	}
}
