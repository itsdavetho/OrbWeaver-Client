package com.pepperoni.orbweaver.packets.incoming.group;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import com.pepperoni.orbweaver.packets.IncomingPacket;
import com.pepperoni.orbweaver.player.OrbWeaverPlayer;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class PlayerList extends IncomingPacket
{
	public PlayerList(byte[] data) throws IOException
	{
		super(data);
	}

	@Override
	public void process(OrbWeaverPlugin plugin) throws IOException
	{
		DataInputStream data = this.getData();
		HashMap<Short, OrbWeaverPlayer> players = new HashMap<>();
		int length = data.readUnsignedShort();
		while (data.available() > 0)
		{
			int userId = data.readUnsignedShort();
			int usernameLength = data.readUnsignedByte();
			byte[] usernameBytes = new byte[usernameLength];
			data.readFully(usernameBytes);
			String username = new String(usernameBytes, StandardCharsets.UTF_8);
			if(username == plugin.getUser().getUsername()) {

			}
			OrbWeaverPlayer orbWeaverPlayer = new OrbWeaverPlayer(username);
			players.put((short) userId, orbWeaverPlayer);
		}
		plugin.setPlayers(players);
	}

}
