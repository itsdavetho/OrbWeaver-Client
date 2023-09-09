package com.pepperoni.orbweaver.packets.incoming.group;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.IncomingPacket;
import java.io.IOException;

public class GroupLeave extends IncomingPacket
{
	public GroupLeave(byte[] data) throws IOException
	{
		super(data);
	}

	@Override
	public void process(OrbWeaverPlugin plugin) {
		plugin.sendPrivateMessage("You have left the OrbWeaver group");
		plugin.getUser().setGroupId(null);
	}
}
