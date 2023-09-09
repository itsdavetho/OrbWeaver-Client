package com.pepperoni.orbweaver.packets.outgoing.group;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.OutgoingPacket;
import com.pepperoni.orbweaver.packets.PacketType;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JoinGroup extends OutgoingPacket
{
	public JoinGroup(OrbWeaverPlugin plugin, String groupId) throws IOException
	{
		super(plugin, PacketType.GROUP_JOIN);
		DataOutputStream data = this.getDataOutputStream();
		byte[] groupBytes = groupId.getBytes(StandardCharsets.UTF_8);
		data.write(groupBytes);
	}
}
