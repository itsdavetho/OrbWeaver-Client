package com.pepperoni.orbweaver.packets.incoming.server;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.IncomingPacket;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ServerInfo extends IncomingPacket
{

	public ServerInfo(byte[] data) throws IOException
	{
		super(data);
	}

	@Override
	public void process(OrbWeaverPlugin plugin) throws IOException
	{
		DataInputStream data = this.getData();
		int playersOnline = data.readUnsignedByte();
		int maxPlayers = data.readUnsignedByte();
		int serverTitleLength = data.readUnsignedByte();
		byte[] serverTitleData = new byte[serverTitleLength];
		data.readFully(serverTitleData);
		String serverTitle = new String(serverTitleData, StandardCharsets.UTF_8);

		System.out.println("server info received: " + serverTitle);
		plugin.setPlayersOnline(playersOnline);
		plugin.setMaxPlayers(maxPlayers);
		plugin.setServerTitle(serverTitle);
	}
}
