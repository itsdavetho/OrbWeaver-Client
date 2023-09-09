package com.pepperoni.orbweaver.packets.incoming.user;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.IncomingPacket;
import java.io.IOException;

public class LoggedOut extends IncomingPacket
{
	public LoggedOut(byte[] data) throws IOException
	{
		super(data);
	}

	@Override
	public void process(OrbWeaverPlugin plugin) throws IOException
	{
		plugin.getUser().setLoggedIn(false);
		plugin.setServerTitle("OrbWeaver");
		plugin.setPlayersOnline(0);
		plugin.setMaxPlayers(0);
		plugin.getUser().setJWT(null);
		plugin.getUser().setGroupId(null);
	}
}
